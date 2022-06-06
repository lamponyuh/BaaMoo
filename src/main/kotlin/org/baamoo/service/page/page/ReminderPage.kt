package org.baamoo.service.page.page

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import org.baamoo.repository.Cache
import org.baamoo.model.FeatureType
import org.baamoo.model.FeatureType.CREATE_REMINDER
import org.baamoo.model.PageType
import org.baamoo.model.PageType.REMINDER
import org.baamoo.model.State
import org.baamoo.repository.ReminderRepository
import org.baamoo.service.page.Page
import org.baamoo.service.page.PageProducer
import org.baamoo.service.page.PageRegister
import org.baamoo.service.update.AbstractUpdate
import org.springframework.stereotype.Component
import java.text.MessageFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Component
class ReminderPage(
    private val pageRegister: PageRegister,
    private val pageProducer: PageProducer,
    private val cache: Cache,
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
        val newState = State(REMINDER)
        cache.put(update.getUser(), newState)
        return newState
    }

    override suspend fun render(update: AbstractUpdate) {
        pageProducer.renderPage(update, REMINDER, getStartText(update))
    }

    override suspend fun renderEdit(update: AbstractUpdate) {
        pageProducer.editPage(update, REMINDER, getStartText(update))
    }

    override suspend fun getStartText(update: AbstractUpdate): String {
        val reminders = reminderRepository.findByUserId(update.getUser().id())
            .filter { it.date.isAfter(LocalDate.now()) }
            .toList()
            .sortedBy { it.date }

        return if (reminders.isEmpty()) {
            EMPTY_TEXT
        } else
            MessageFormat.format(TEXT_WITH_FIRST_REMINDER, reminders.first().name, reminders.first().date.format(FORMATTER))
    }

    companion object{
        val FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        const val EMPTY_TEXT = "Ты можешь управлять своими напоминаниями. Пока не создано ни одного напоминания!"

        const val TEXT_WITH_FIRST_REMINDER = "Ты можешь управлять своими напоминаниями. Кстати, вот ближайшее:\n\n" +
                "\"{0}\" - {1}\n\n"
    }
}