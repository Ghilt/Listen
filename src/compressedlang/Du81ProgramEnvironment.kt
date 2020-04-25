package compressedlang

// In ~90% of cases when you program something less trivial it feels like you end up with a desire for something
// singletonny for the case of simplicity. Makes me think of Android 'Context',if it was how it started, and they just
// decided to send it in every method

object Du81ProgramEnvironment {

    private var repository: FunctionRepository? = null

    fun initializeRepo(repo: FunctionRepository) {
        if (repository != null) {
            throw DeveloperError("What are you even doing? Double initialization functionRepository")
        } else {
            this.repository = repo
        }
    }

    val repo
        get() = repository ?: throw DeveloperError("")

    fun getDiagnosticsString(func: Function) = repository?.getDiagnosticsString(func)
        ?: throw DeveloperError("What are you even doing? You forgot to initialize functionRepository")

    internal fun for_test_only_ResetEnvironment() {
        if (isJUnitTest()) {
            repository = null
        } else {
            throw DeveloperError("Do not run test methods from outside jUnit please. Discipline.")
        }
    }

    private fun isJUnitTest(): Boolean {
        val stackTrace = Thread.currentThread().stackTrace
        val list = listOf(*stackTrace)
        for (element in list) {
            if (element.className.startsWith("org.junit.")) {
                return true
            }
        }
        return false
    }
}