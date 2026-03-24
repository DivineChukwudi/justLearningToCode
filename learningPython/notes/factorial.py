userEnd = int(input("Enter ending number: "))
factorial = 1

for x in range(1, userEnd + 1):
    factorial = factorial * x

if len(str(factorial)) >= 4:
    factorial = f"{factorial:,}"
print(factorial)
    