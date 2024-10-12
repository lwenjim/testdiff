package main

import (
	"os"

	"gopkg.in/gomail.v2"
)

func main() {
	m := gomail.NewMessage()
	m.SetHeader("From", os.Getenv("EMAIL_ACCOUNT"))
	m.SetHeader("To", os.Getenv("EMAIL_ACCOUNT"), os.Getenv("EMAIL_ACCOUNT"))
	m.SetAddressHeader("Cc", os.Getenv("EMAIL_ACCOUNT"), "Dan")
	m.SetHeader("Subject", "Hello!")
	m.SetBody("text/html", "Hello <b>Bob</b> and <i>Cora</i>!")
	m.Attach("/var/log/system.log")

	d := gomail.NewDialer("smtp.qq.com", 465, os.Getenv("EMAIL_ACCOUNT"), os.Getenv("EMAIL_TOKEN"))
	if err := d.DialAndSend(m); err != nil {
		panic(err)
	}
}
