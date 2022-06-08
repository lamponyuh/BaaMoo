package org.baamoo.service.page.feature

import com.pengrad.telegrambot.model.User
import org.baamoo.repository.feature.CreateReminderEntity
import org.baamoo.repository.feature.CreateReminderRepository
import org.baamoo.model.FeatureType.CREATE_REMINDER
import org.baamoo.model.PageType
import org.baamoo.model.PageType.REMINDER
import org.baamoo.repository.Reminder
import org.baamoo.repository.ReminderRepository
import org.baamoo.repository.State
import org.baamoo.repository.UserSessionRepository
import org.baamoo.service.page.Feature
import org.baamoo.service.page.FeatureRegister
import org.baamoo.service.page.PageProducer
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Component
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Component
class CreateReminderFeature(
    private val userSessionRepository: UserSessionRepository,
    private val pageProducer: PageProducer,
    private val featureRegister: FeatureRegister,
    private val reminderRepository: ReminderRepository,
    private val createReminderRepository: CreateReminderRepository
) : Feature() {

    @PostConstruct
    override fun register() {
        featureRegister.createReminderFeature = this
    }

    override suspend fun process(update: AbstractUpdate) {
        val message = update.getMessage().text()
        when(userSessionRepository.findById(update.getUser().id())?.state?.position) {
            INPUT_DATE -> {
                val note = createReminderRepository.findById(update.getUser().id())

                val date: LocalDate

                try {
                    date = LocalDate.parse(message, ExpressCalcFeature.FORMATTER)
                } catch (e: DateTimeException) {
                    pageProducer.delete(update)
                    pageProducer.editPage(update.getUser(), note?.messageId!!, CREATE_REMINDER, INPUT_DATE, ERROR_DATE_TEXT)
                    return
                }

                note?.date = date
                createReminderRepository.save(note!!)

                pageProducer.delete(update)
                pageProducer.editPage(update.getUser(), note.messageId, CREATE_REMINDER, INPUT_REMINDER_NAME, INPUT_REMINDER_NAME_TEXT)
                updateState(update.getUser(), State(PageType.CALC_LAMBING_DATE, CREATE_REMINDER, INPUT_REMINDER_NAME))
            }
            INPUT_REMINDER_NAME -> {
                val note = createReminderRepository.findById(update.getUser().id())

                reminderRepository.save(Reminder(
                    userId = update.getUser().id(),
                    date = note?.date!!,
                    name = message,
                    expireAfter = note.date?.plusDays(1)
                ))

                pageProducer.delete(update)
                pageProducer.editPage(update.getUser(), note.messageId, CREATE_REMINDER, FINAL, FINAL_TEXT)
                updateState(update.getUser(), State(PageType.CALC_LAMBING_DATE, CREATE_REMINDER, FINAL))
            }
            FINAL -> {
                when (update.update().callbackQuery().data()) {
                    "RETURN_TO_MAIN" -> pageProducer.open(update, PageType.MAIN)
                }
            }
        }
    }

    suspend fun initiateWithDate(update: AbstractUpdate, date: LocalDate) {
        val messageId = update.getMessage().messageId()
        createReminderRepository.save(CreateReminderEntity(
            userId = update.getUser().id(),
            messageId = messageId,
            date = date
        ))
        pageProducer.editPage(update.getUser(), messageId, CREATE_REMINDER, INPUT_REMINDER_NAME, INPUT_REMINDER_NAME_TEXT)
        updateState(update.getUser(), State(PageType.CALC_LAMBING_DATE, CREATE_REMINDER, INPUT_REMINDER_NAME))
    }

    override suspend fun updateState(user: User, state: State): State {
        val currentSession = userSessionRepository.findById(user.id())
        userSessionRepository.save(currentSession?.copy(
            state = state,
            expiredTime = LocalDateTime.now().plusMinutes(10))!!)
        return state
    }

    override suspend fun updateOnNewState(update: AbstractUpdate): State {
        val currentSession = userSessionRepository.findById(update.getUser().id())
        val newState = State(REMINDER, CREATE_REMINDER, INPUT_DATE)

        userSessionRepository.save(currentSession!!.copy(
            expiredTime = LocalDateTime.now().plusMinutes(10),
            state = newState
        ))

        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, CREATE_REMINDER, INPUT_DATE, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        createReminderRepository.save(CreateReminderEntity(
            userId = update.getUser().id(),
            messageId = update.getMessage().messageId(),
        ))
        pageProducer.editPage(update, CREATE_REMINDER, INPUT_DATE, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        return getStartText()
    }

    override suspend fun getStartText(): String {
        return INSERT_DATE_START_TEXT
    }

    companion object{
        val FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        const val ERROR_DATE_TEXT = "Неверно введена дата.\nДата должна быть вида: 22.03.2021"

        const val INSERT_DATE_START_TEXT = "Введите дату, которую хотите сохранить в формате 22.03.2021"
        const val INPUT_REMINDER_NAME_TEXT = "Введите название будущего напоминания. Например: \"Окот козы Агата\""
        const val FINAL_TEXT = "Отлично! Напоминание создано!"

        const val INPUT_DATE = 0
        const val INPUT_REMINDER_NAME = 1
        const val FINAL = 2
    }
}