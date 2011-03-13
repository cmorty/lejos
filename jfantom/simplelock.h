/**
 * SimpleLock implements a re-entrant lock.
 */
class SimpleLock;

#ifdef __WIN32__
#include "simplelock_win32.h"
#else
#include "simplelock_unix.h"
#endif

/**
 * Just declare a local object of this class to aquire a lock.
 * It is released automatically.
 */
class SimpleLockHandle
{
private:
	SimpleLock *ptr;
public:
	SimpleLockHandle(SimpleLock &lock)
	{
		ptr = &lock;
		ptr->lock();
	}

	~SimpleLockHandle()
	{
		ptr->unlock();
	}
};
