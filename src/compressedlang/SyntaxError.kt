package compressedlang

class SyntaxError(message: String) : Throwable(message)

class Du81AttemptingToFetchNonExistingListError(listCount: Int, index: Int) :
    Throwable("Attempted to access list at position: $index, but there were only $listCount list${if (listCount == 1) "" else "s"} in memory")
