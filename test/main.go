package main

import (
	"fmt"
	"os"
	"strconv"
)

func fib(n uint) uint {
	switch n {
	case 0:
		return 0
	case 1:
		return 1
	case 2:
		return 1
	default:
		return (fib(n-2) + fib(n-1))
	}
}

func main() {
	args := os.Args
	for i := 1; i < len(args); i++ {
		v1, err := strconv.Atoi(args[i])
		if err != nil {
			panic(err)
		}
		var arg uint = uint(v1)
		fmt.Printf("The %dth Fibonacci number is %d\n", arg, fib(arg))
	}
}
