#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main() {
    int a;
    int b;
    printf("a的内存地址：%p \n", &a);
    printf("b的内存地址：%p \n", &b);

    char buf[32];
    printf("buf[0]的内存地址：%p \n", &buf[0]);
    printf("buf[1]的内存地址：%p \n", &buf[1]);

    int *c = malloc(sizeof(int));
    int *d = malloc(sizeof(int));
    printf("*c的内存地址：%p \n", c);
    printf("*d的内存地址：%p \n", d);

    return 0;
}
