#include <pthread.h>

class SimpleLock
{
private:
	pthread_mutex_t mutex;
public:
	SimpleLock()
	{
		pthread_mutexattr_t mutexattr;

		pthread_mutexattr_init(&mutexattr);
		pthread_mutexattr_settype(&mutexattr, PTHREAD_MUTEX_RECURSIVE);
		pthread_mutex_init(&mutex, &mutexattr);
		pthread_mutexattr_destroy(&mutexattr);
	}

	~SimpleLock()
	{
		pthread_mutex_destroy(&mutex);
	}

	void lock()
	{
		pthread_mutex_lock(&mutex);
	}

	void unlock()
	{
		pthread_mutex_unlock(&mutex);
	}
};
