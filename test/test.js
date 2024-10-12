async function f() {
    let promise = new Promise(
        (resolve, reject) => {setTimeout(() => resolve('done'), 1000)});
    let result = await promise;
    console.log(result);
}

f();

// async function f() {
//     return Promise.resolve(1);
// }
// f().then(console.log);

// var arr = [];
// for (var i = 0; i < 10; i++) {
//     arr[i] = function() {
//         console.log(i)
//     }
// }
// arr[0]()