package org.baamoo.service.page.feature

import com.pengrad.telegrambot.model.User
import org.baamoo.model.FeatureType.EXPRESS_CALC_FEED
import org.baamoo.model.PageType.CALC_FEED
import org.baamoo.model.PageType.MAIN
import org.baamoo.repository.State
import org.baamoo.repository.UserSessionRepository
import org.baamoo.repository.feature.ExpressCalcFeedEntity
import org.baamoo.repository.feature.ExpressCalcFeedRepository
import org.baamoo.repository.feature.Feed
import org.baamoo.service.page.Feature
import org.baamoo.service.page.FeatureRegister
import org.baamoo.service.page.PageProducer
import org.baamoo.service.update.AbstractUpdate
import org.baamoo.service.update.UpdateType.CALLBACK_QUERY
import org.baamoo.service.update.UpdateType.MESSAGE
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Component
class ExpressCalcFeedFeature(
    private val featureRegister: FeatureRegister,
    private val userSessionRepository: UserSessionRepository,
    private val pageProducer: PageProducer,
    private val expressCalcFeedRepository: ExpressCalcFeedRepository,
) : Feature() {

    @PostConstruct
    override fun register() {
        featureRegister.expressCalcFeedFeature = this
    }

    override suspend fun process(update: AbstractUpdate) {
        val currentState = userSessionRepository.findById(update.getUser().id())!!
        when (currentState.state.position) {
            CHOSE_PERIOD -> {
                var userInput = 0
                when (update.getType()) {
                    MESSAGE -> {
                        try {
                            userInput = update.update().message().text().toInt()
                        } catch (e: NumberFormatException) {
                            pageProducer.delete(update)
                            pageProducer.editPage(update.getUser(), currentState.sessionMessageId!!, EXPRESS_CALC_FEED, CHOSE_PERIOD, CHOSE_PERIOD_ERROR_TEXT)
                            return
                        }
                        pageProducer.delete(update)
                        pageProducer.editPage(update.getUser(), currentState.sessionMessageId!!, EXPRESS_CALC_FEED, INPUT_FEED, INPUT_FEED_TEXT)
                    }
                    CALLBACK_QUERY -> {
                        userInput = update.update().callbackQuery().data().toInt()
                        pageProducer.editPage(update.getUser(), update.getMessage().messageId(), EXPRESS_CALC_FEED, INPUT_FEED, INPUT_FEED_TEXT)
                    }
                    else -> {}
                }

                expressCalcFeedRepository.save(ExpressCalcFeedEntity(
                    userId = update.getUser().id(),
                    periodDays = userInput
                ))
                updateState(update.getUser(), State(CALC_FEED, EXPRESS_CALC_FEED, INPUT_FEED))
            }
            INPUT_FEED -> {
                val userInput = update.update().message().text()
                val note = expressCalcFeedRepository.findById(update.getUser().id())!!

                try {
                    note.feedList.add(userInput.toFeed())
                } catch (e: IllegalArgumentException) {
                    pageProducer.delete(update)
                    pageProducer.editPage(update.getUser(), currentState.sessionMessageId!!, EXPRESS_CALC_FEED, INPUT_FEED, INPUT_FEED_ERROR_TEXT)
                    return
                }

                expressCalcFeedRepository.save(note)

                pageProducer.delete(update)
                pageProducer.editPage(update.getUser(), currentState.sessionMessageId!!, EXPRESS_CALC_FEED, CALC_OR_MORE, CALC_OR_MORE_TEXT)
                updateState(update.getUser(), State(CALC_FEED, EXPRESS_CALC_FEED, CALC_OR_MORE))
            }
            CALC_OR_MORE -> {
                val feedInfo = expressCalcFeedRepository.findById(update.getUser().id())!!
                when(update.update().callbackQuery().data()) {
                    "MORE" -> {
                        pageProducer.editPage(update.getUser(), update.getMessage().messageId(), EXPRESS_CALC_FEED, INPUT_FEED, INPUT_FEED_TEXT)
                        updateState(update.getUser(), State(CALC_FEED, EXPRESS_CALC_FEED, INPUT_FEED))
                    }
                    "CALC" -> {
                        pageProducer.editPage(update.getUser(), update.getMessage().messageId(), EXPRESS_CALC_FEED, CALC, calcFeedAmount(feedInfo))
                        updateState(update.getUser(), State(CALC_FEED, EXPRESS_CALC_FEED, CALC))
                    }
                    else -> {}
                }
            }
            CALC -> {
                when(update.update().callbackQuery().data()) {
                    "RETURN_TO_MAIN" -> pageProducer.open(update, MAIN)
                    else -> {}
                }
            }
        }
    }

    override suspend fun updateState(user: User, state: State) : State {
        val currentSession = userSessionRepository.findById(user.id())!!
        userSessionRepository.save(currentSession.copy(
            state = state,
            expiredTime = LocalDateTime.now().plusMinutes(10)))
        return state
    }

    override suspend fun updateOnNewState(update: AbstractUpdate): State {
        val currentSession = userSessionRepository.findById(update.getUser().id())
        val newState = State(CALC_FEED, EXPRESS_CALC_FEED, CHOSE_PERIOD)

        userSessionRepository.save(currentSession!!.copy(
            expiredTime = LocalDateTime.now().plusMinutes(10),
            state = newState
        ))

        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, EXPRESS_CALC_FEED, CHOSE_PERIOD, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, EXPRESS_CALC_FEED, CHOSE_PERIOD, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        return getStartText()
    }

    override suspend fun getStartText(): String {
        return START_TEXT
    }

    private fun String.toFeed(): Feed {
        val result = try {
            val split = this.split(" ")

            val lastIndex = split.lastIndex
            val price = split[lastIndex].toDouble()
            val amount = split[lastIndex - 1].toDouble()
            val name = split.take(lastIndex - 1).joinToString(separator = " ")

            assertFields(price, amount, name)
            Feed(name, amount, price)
        } catch (e: Exception) {
            throw IllegalArgumentException()
        }
        return result
    }

    private fun assertFields(price: Double, amount: Double, name: String) {
        if (price < 1 || amount < 1) throw IllegalArgumentException()
        if (name.isBlank()) throw IllegalArgumentException()
    }

    private suspend fun calcFeedAmount(feedInfo: ExpressCalcFeedEntity): String {
        feedInfo.feedList.map {
            it.kgAmount *= feedInfo.periodDays
            it.price *= it.kgAmount
        }

        var result = ""

        feedInfo.feedList.forEach { result += String.format(FEED_RESULT_INFO_TEXT, it.name, it.kgAmount, it.price) }

        val priceSum = String.format("%.1f", feedInfo.feedList.sumOf { it.price })
        val amountFeed = String.format("%.1f", feedInfo.feedList.sumOf { it.kgAmount })

        return String.format(RESULT_START_TEXT, feedInfo.periodDays) + result +
                String.format(RESULT_END_TEXT, priceSum, amountFeed)
    }

    companion object{
        const val CHOSE_PERIOD_ERROR_TEXT = "Число дней введено не верно. Выберите период или введите целое число дней"
        const val INPUT_FEED_ERROR_TEXT = "Неверно введены данные о корме.\nВведите данные через пробел, например: Овес 15 245.70"
        const val START_TEXT = "Выберете, за какой период необходимо посчитать или введите вручную количество дней"
        const val INPUT_FEED_TEXT = "Введите название корма, количество потребляемого корма 1 головой в сутки и цену за кг.\n" +
                "Вводить через пробел: Зерно обыкновенное 15 150"
        const val CALC_OR_MORE_TEXT = "Добавить еще тип корма или посчитать итог?"

        const val RESULT_START_TEXT = "Для выбранного количества дней (%s), на 1 голову необходимо:\n"
        const val FEED_RESULT_INFO_TEXT = "\n\"%s\" - %.1fкг, который обойдется в %.1fр."
        const val RESULT_END_TEXT = "\n\nИтого: %sр. за %sкг корма"

        const val CHOSE_PERIOD = 0
        const val INPUT_FEED = 1
        const val CALC_OR_MORE = 2
        const val CALC = 3
    }
}