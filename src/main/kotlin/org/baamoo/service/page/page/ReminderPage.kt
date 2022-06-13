package org.baamoo.service.page.page

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.baamoo.model.FeatureType
import org.baamoo.model.FeatureType.CREATE_REMINDER
import org.baamoo.model.PageType
import org.baamoo.model.PageType.REMINDER
import org.baamoo.repository.Reminder
import org.baamoo.repository.ReminderRepository
import org.baamoo.repository.State
import org.baamoo.repository.UserSessionRepository
import org.baamoo.service.page.Page
import org.baamoo.service.page.PageProducer
import org.baamoo.service.page.PageRegister
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Component
import java.text.MessageFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Component
class ReminderPage(
    private val pageRegister: PageRegister,
    private val pageProducer: PageProducer,
    private val userSessionRepository: UserSessionRepository,
    private val reminderRepository: ReminderRepository,
) : Page() {

    @PostConstruct
    override fun register() {
        pageRegister.reminderPage = this
    }

    override suspend fun process(update: AbstractUpdate) {
        val updateData = update.update().callbackQuery().data()

        if (isPage(updateData)) {
            when(PageType.valueOf(updateData)) {
                else -> {}
            }
        } else {
            when(FeatureType.valueOf(updateData)) {
//                REMINDERS_LIST -> pageProducer.open(update, REMINDERS_LIST)
                CREATE_REMINDER -> pageProducer.open(update, CREATE_REMINDER)
                else -> {}
            }
        }
    }

    override suspend fun updateOnNewState(update: AbstractUpdate): State {
        val currentSession = userSessionRepository.findById(update.getUser().id())
        val newState = State(REMINDER)

        userSessionRepository.save(currentSession!!.copy(
            expiredTime = LocalDateTime.now().plusMinutes(10),
            state = newState
        ))

        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, REMINDER, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, REMINDER, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        val reminders = reminderRepository.findByUserIdAndDateAfterOrderByDate(update.getUser().id(), LocalDate.now())
            .take(3)
            .toList()

        return if (reminders.isEmpty()) {
            EMPTY_TEXT
        } else {
            val remindersText = reminders.toText()
            TEXT_WITH_FIRST_REMINDER + remindersText
        }
    }

    override suspend fun getStartText(): String {
        TODO("Not yet implemented")
    }

    private fun List<Reminder>.toText() : String {
        var result = ""
        this.forEach{ result += MessageFormat.format(REMINDER_FORMAT, it.name, it.date.format(DATE_FORMATTER)) }
        return result
    }

    companion object{
        val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        const val EMPTY_TEXT = "Ты можешь управлять своими напоминаниями. Пока не создано ни одного напоминания!"
        const val TEXT_WITH_FIRST_REMINDER = "Ты можешь управлять своими напоминаниями. Кстати, вот ближайшие:\n\n"
        const val REMINDER_FORMAT = "\"{0}\" - {1}\n\n"
    }
}