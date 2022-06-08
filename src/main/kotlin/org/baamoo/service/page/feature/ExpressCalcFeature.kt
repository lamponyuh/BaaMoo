package org.baamoo.service.page.feature

import com.pengrad.telegrambot.model.User
import org.baamoo.repository.feature.ExpressCalcEntity
import org.baamoo.model.BeastType
import org.baamoo.model.FeatureType.EXPRESS_CALC
import org.baamoo.model.PageType.CALC_LAMBING_DATE
import org.baamoo.model.PageType.MAIN
import org.baamoo.repository.State
import org.baamoo.repository.UserSession
import org.baamoo.repository.UserSessionRepository
import org.baamoo.repository.feature.ExpressCalcRepository
import org.baamoo.service.page.Feature
import org.baamoo.service.page.FeatureRegister
import org.baamoo.service.page.PageProducer
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Component
import java.text.MessageFormat
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Component
class ExpressCalcFeature(
    private val featureRegister: FeatureRegister,
    private val userSessionRepository: UserSessionRepository,
    private val pageProducer: PageProducer,
    private val expressCalcRepository: ExpressCalcRepository,
) : Feature() {

    @PostConstruct
    override fun register() {
        featureRegister.expressCalcFeature = this
    }

    override suspend fun process(update: AbstractUpdate) {
        val currentState = userSessionRepository.findById(update.getUser().id())?.state!!
        when (currentState.position) {
            CHOSE_BEAST -> {
                val callbackQuery = update.update().callbackQuery()
                expressCalcRepository.save(ExpressCalcEntity(
                    userId = update.getUser().id(),
                    beastType = BeastType.valueOf(callbackQuery.data()),
                    messageId = callbackQuery.message().messageId()
                ))
                pageProducer.editPage(update, EXPRESS_CALC, INPUT_DATE, INPUT_DATE_TEXT)
                updateState(update.getUser(), State(CALC_LAMBING_DATE, EXPRESS_CALC, INPUT_DATE))
            }
            INPUT_DATE -> {
                val userDate = update.update().message().text()
                val note = expressCalcRepository.findById(update.getUser().id())!!
                val gestation = note.beastType.gestation.toLong()
                val gestationDate: LocalDate

                try {
                    gestationDate = LocalDate.parse(userDate, FORMATTER).plusDays(gestation)
                } catch (e: DateTimeException) {
                    pageProducer.delete(update)
                    pageProducer.editPage(update.getUser(), note.messageId, EXPRESS_CALC, INPUT_DATE, ERROR_DATE_TEXT)
                    return
                }

                note.date = gestationDate
                expressCalcRepository.save(note)

                pageProducer.delete(update)
                pageProducer.editPage(update.getUser(), note.messageId, EXPRESS_CALC, FINAL,
                    MessageFormat.format(FINAL_TEXT, note.beastType.title, userDate, gestationDate.format(FORMATTER)))
                updateState(update.getUser(), State(CALC_LAMBING_DATE, EXPRESS_CALC, FINAL))
            }
            FINAL -> {
                when(update.update().callbackQuery().data()) {
                    "RETURN_TO_MAIN" -> pageProducer.open(update, MAIN)
                    "CREATE_REMINDER" -> {
                        val note = expressCalcRepository.findById(update.getUser().id())!!
                        featureRegister.createReminderFeature?.initiateWithDate(update, note.date!!)
                    }
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
        val newState = State(CALC_LAMBING_DATE, EXPRESS_CALC, CHOSE_BEAST)

        userSessionRepository.save(currentSession!!.copy(
            expiredTime = LocalDateTime.now().plusMinutes(10),
            state = newState
        ))

        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, EXPRESS_CALC, CHOSE_BEAST, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, EXPRESS_CALC, CHOSE_BEAST, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        return getStartText()
    }

    override suspend fun getStartText(): String {
        return START_TEXT
    }

    companion object{
        val FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        const val START_TEXT = "Чтобы посчитать дату рождения выберите категорию животного"
        const val INPUT_DATE_TEXT = "Введите дату с момента покрытия(закладки яиц) в формате 01.06.2021"
        const val ERROR_DATE_TEXT = "Неверно введена дата.\nДата должна быть вида: 01.06.2021"
        const val FINAL_TEXT = "\"{0}\" - с датой покрытия (закладки) {1}, рождение будет {2}"

        const val CHOSE_BEAST = 0
        const val INPUT_DATE = 1
        const val FINAL = 2
    }
}