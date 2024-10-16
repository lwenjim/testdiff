/*thread_example.c :  c multiple thread programming in linux
 *author : falcon
 *E-mail : tunzhj03@st.lzu.edu.cn
 */
#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <unistd.h>
#define MAX 10
pthread_t thread[2];
pthread_mutex_t mut;
int number = 0, i;
void *thread1() {
    printf("thread1 : I'm thread 1\n");
    for (i = 0; i < MAX; i++) {
        printf("thread1 : number = %d\n", number);
        pthread_mutex_lock(&mut);
        number++;
        pthread_mutex_unlock(&mut);
        sleep(2);
    }
    printf("thread1 :主函数在等我完成任务吗？\n");
    pthread_exit(NULL);
}
void *thread2() {
    printf("thread2 : I'm thread 2\n");
    for (i = 0; i < MAX; i++) {
        printf("thread2 : number = %d\n", number);
        pthread_mutex_lock(&mut);
        number++;
        pthread_mutex_unlock(&mut);
        sleep(3);
    }
    printf("thread2 :主函数在等我完成任务吗？\n");
    pthread_exit(NULL);
}
void thread_create(void) {
    int temp;
    memset(&thread, 0, sizeof(thread));  // comment1
    /*创建线程*/
    if ((temp = pthread_create(&thread[0], NULL, thread1, NULL)) !=
        0)  // comment2
        printf("线程1创建失败!\n");
    else
        printf("线程1被创建\n");
    if ((temp = pthread_create(&thread[1], NULL, thread2, NULL)) !=
        0)  // comment3
        printf("线程2创建失败");
    else
        printf("线程2被创建\n");
}
void thread_wait(void) {
    /*等待线程结束*/
    if (thread[0] != 0) {  // comment4
        pthread_join(thread[0], NULL);
        printf("线程1已经结束\n");
    }
    if (thread[1] != 0) {  // comment5
        pthread_join(thread[1], NULL);
        printf("线程2已经结束\n");
    }
}
int main_pre() {
    // pthread_mutex_init(&mut, NULL);
    // printf("我是主函数哦，我正在创建线程，呵呵\n");
    // thread_create();
    // printf("我是主函数哦，我正在等待线程完成任务阿，呵呵\n");
    // thread_wait();

    printf("%f\n", 0.1 + 0.2);
    return 0;
}

#include <stdio.h>
#include <stdlib.h>

long long fibonacci(int n);

int main(int argc, char *argv[]) {
    if (argc != 2) {
        printf("使用方法: ./fib-c NUMBER\n");
        return 1;
    }

    int n = atoi(argv[1]);
    printf("The %dth Fibonacci number is %lld\n", n, fibonacci(n));

    return 0;
}

long long fibonacci(int n) {
    if (n <= 1)
        return n;
    else
        return fibonacci(n - 1) + fibonacci(n - 2);
}

