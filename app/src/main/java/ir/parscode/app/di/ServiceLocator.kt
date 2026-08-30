package ir.parscode.app.di

import android.content.Context
import androidx.room.Room
import ir.parscode.app.data.local.AppDatabase
import ir.parscode.app.data.local.entity.WeekEntity
import ir.parscode.app.data.local.entity.GoalEntity
import ir.parscode.app.data.local.entity.TaskEntity
import ir.parscode.app.data.repository.HabitRepositoryImpl
import ir.parscode.app.data.repository.TaskRepositoryImpl
import ir.parscode.app.domain.repository.HabitRepository
import ir.parscode.app.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ServiceLocator {
    private lateinit var db: AppDatabase

    lateinit var appContext: Context
        private set
    lateinit var taskRepository: TaskRepository
        private set
    lateinit var habitRepository: HabitRepository
        private set
    lateinit var automationStore: ir.parscode.app.worker.AutomationStore
        private set

    fun init(context: Context) {
        if (::db.isInitialized) return
        appContext = context.applicationContext
        db = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration(true)
            .build()

        taskRepository = TaskRepositoryImpl(db.taskDao())
        habitRepository = HabitRepositoryImpl(db.habitDao())
        settingsDataStore = ir.parscode.app.util.SettingsDataStore(context.applicationContext)
        automationStore = ir.parscode.app.worker.AutomationStore(context.applicationContext)

        seedIfEmpty(context)
    }

    val goalDao get() = db.goalDao()
    val weekDao get() = db.weekDao()
    val taskDao get() = db.taskDao()
    val pomodoroDao get() = db.pomodoroDao()
    val libraryDao get() = db.libraryDao()
    val financeDao get() = db.financeDao()

    lateinit var settingsDataStore: ir.parscode.app.util.SettingsDataStore
        private set

    private fun seedIfEmpty(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            if (db.weekDao().count() == 0) {
                // (title, description, progress%) - status is always derived live from
                // progress (see ProgressStatus) so it can never drift out of sync.
                val weeks = listOf(
                    Triple("معرفی و برنامه‌ریزی", "آشنایی با ساختار برنامه و تعیین اهداف اولیه", 100),
                    Triple("شروع پایه‌سازی", "ایجاد پایه علمی و عادت‌های مطالعاتی", 75),
                    Triple("تقویت و تمرین", "تمرین مستمر و رفع نقاط ضعف", 60),
                    Triple("پیشروی هدفمند", "پیشرفت هدفمند در مباحث تخصصی‌تر", 45),
                    Triple("ارزیابی و اصلاح", "بررسی عملکرد و اصلاح برنامه", 30),
                    Triple("تمرکز تخصصی", "تمرکز بر مباحث تخصصی و دشوار", 20),
                    Triple("اجرای عمیق", "تمرین عمیق و پرحجم برای تثبیت مهارت", 10),
                    Triple("مرور و تثبیت", "مرور جامع مطالب گذشته", 0),
                    Triple("تمرین پیشرفته", "حل تست‌های پیشرفته و ترکیبی", 0),
                    Triple("شبیه‌سازی واقعی", "برگزاری آزمون‌های شبیه‌سازی‌شده", 0),
                    Triple("جمع‌بندی نهایی", "جمع‌بندی کل مباحث دوره", 0),
                    Triple("آماده‌سازی نهایی", "آماده‌سازی روانی و علمی نهایی", 0),
                )
                val weekIds = weeks.mapIndexed { idx, (t, d, p) ->
                    db.weekDao().upsert(
                        WeekEntity(weekNumber = idx + 1, title = t, description = d, progressPercent = p, status = "")
                    )
                }

                // Sample goals + tasks for week 3, matching the reference "برنامه ۱۲ هفته‌ای" screen.
                val week3Id = weekIds[2]
                data class SeedGoal(val title: String, val desc: String, val progress: Int, val doneTasks: Int, val pendingTasks: Int, val todoTasks: Int)
                val goals = listOf(
                    SeedGoal("مطالعه فصل‌های ریاضی", "مطالعه و حل تمرین فصل‌های ۱ تا ۳", 100, doneTasks = 8, pendingTasks = 0, todoTasks = 0),
                    SeedGoal("حل تست‌های پایه", "حل حداقل ۵۰۰ تست پایه", 75, doneTasks = 0, pendingTasks = 3, todoTasks = 0),
                    SeedGoal("مرور نکات مهم دروس", "مرور و خلاصه‌نویسی نکات مهم", 50, doneTasks = 0, pendingTasks = 0, todoTasks = 1),
                    SeedGoal("تقویت مهارت تست‌زنی", "افزایش سرعت و دقت در تست زنی", 25, doneTasks = 0, pendingTasks = 0, todoTasks = 1),
                    SeedGoal("آزمون آزمایشی اول", "شرکت در آزمون و تحلیل نتایج", 0, doneTasks = 0, pendingTasks = 0, todoTasks = 0),
                )
                val today = ir.parscode.app.util.DateUtils.todayIso()
                goals.forEach { g ->
                    val goalId = db.goalDao().upsert(
                        GoalEntity(title = g.title, description = g.desc, progressPercent = g.progress, isCompleted = g.progress >= 100, createdDateIso = today, weekId = week3Id)
                    )
                    var n = 0
                    repeat(g.doneTasks) { n++; db.taskDao().upsert(TaskEntity(dateIso = today, title = "${g.title} - وظیفه ${n}", weekId = week3Id, goalId = goalId, isDone = true, taskStatus = "DONE")) }
                    repeat(g.pendingTasks) { n++; db.taskDao().upsert(TaskEntity(dateIso = today, title = "${g.title} - وظیفه ${n}", weekId = week3Id, goalId = goalId, taskStatus = "PENDING")) }
                    repeat(g.todoTasks) { n++; db.taskDao().upsert(TaskEntity(dateIso = today, title = "${g.title} - وظیفه ${n}", weekId = week3Id, goalId = goalId, taskStatus = "TODO")) }
                }
            }
        }
    }
}
