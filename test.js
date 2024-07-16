var arr = [];
for (var i = 0; i < 10; i++) {
    arr[i] = function() {
        console.log(i)
    }
}
arr[0]()
