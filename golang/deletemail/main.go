package main

import (
	"os"

	"gopkg.in/gomail.v2"
)

func main() {
	account := os.Getenv("EMAIL_ACCOUNT")
	token := os.Getenv("EMAIL_TOKEN")
	m := gomail.NewMessage()
	m.SetHeader("From", account)
	m.SetHeader("To", account, account)
	m.SetAddressHeader("Cc", account, "Dan")
	m.SetHeader("Subject", "Hello!")
	m.SetBody("text/html", "Hello <b>Bob</b> and <i>Cora</i>!")
	m.Attach("/var/log/system.log")

	d := gomail.NewDialer("smtp.qq.com", 465, account, token)
	if err := d.DialAndSend(m); err != nil {
		panic(err)
	}
}
