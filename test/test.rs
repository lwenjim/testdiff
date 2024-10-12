use std::env;
use std::str::FromStr;

fn fib(n: u64) -> u64 {
    match n {
        0 => 0,
        1 => 1,
        2 => 1,
        _ => fib(n - 1) + fib(n - 2),
    }
}

fn main() {
    let mut args = Vec::new();
    for arg in env::args().skip(1) {
        args.push(u64::from_str(&arg).expect("error parsing argument"));
    }

    if args.len() == 0 {
        eprintln!("Usage: fib <number>");
        std::process::exit(1);
    }

    println!("The {}th Fibonacci number is {}", args[0], fib(args[0]));
}
