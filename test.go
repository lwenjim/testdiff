package main

import (
	"fmt"
	"strings"
)

func main() {
	items := strings.Split("2@qq.com", "@")
	fmt.Printf("%v\n", items[1])
}
