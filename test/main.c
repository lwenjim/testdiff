#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/_pthread/_pthread_mutex_t.h>
#include <sys/_pthread/_pthread_t.h>
#include <unistd.h>

pthread_t thread[2];
pthread_mutex_t mut;

void *say(void *param) {
    char *name = (char *)param;
    for (int i = 0; i < 100; i++) {
        pthread_mutex_lock(&mut);
        printf("%s: %d\n", name, i);
        pthread_mutex_unlock(&mut);
        usleep(1);
    }
    pthread_exit(NULL);
}

int main(int argc, char **argv) {
    char *name1 = "abc";
    char *name2 = "bbb";

    memset(&thread, 0, sizeof(thread));
    pthread_mutex_init(&mut, NULL);

    pthread_create(&thread[0], NULL, say, (void *)name1);
    pthread_create(&thread[1], NULL, say, (void *)name2);
    for (int i = 0; i < sizeof(thread) / sizeof(thread[0]); i++) {
        pthread_join(thread[i], NULL);
    }
    printf("\nmaster process exit\n");
    return 0;
}
