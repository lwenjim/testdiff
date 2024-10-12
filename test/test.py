arr = []
for i in range(1, 100):
    arr.append(lambda: "hello world %s" % i)
print(arr[0]())
