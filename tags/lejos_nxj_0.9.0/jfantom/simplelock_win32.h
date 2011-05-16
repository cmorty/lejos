#include <windows.h>
class SimpleLock
{
private:
	CRITICAL_SECTION cs;
public:
	SimpleLock()
	{
		InitializeCriticalSection(&cs);
	}

	~SimpleLock()
	{
		DeleteCriticalSection(&cs);
	}

	void lock()
	{
		EnterCriticalSection(&cs);
	}

	void unlock()
	{
		LeaveCriticalSection(&cs);
	}
};
