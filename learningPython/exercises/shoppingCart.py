foods = []
prices = []
total = 0
userExit = 'q'

while True:
    food = input("Enter a food to buy(\"q\" to quit): ")
    if food.lower().strip() == userExit:
        break
    else:
        price = float(input(f"Enter the price of a {food}: $"))
        foods.append(food)
        prices.append(price)

userView = input("Would you like to view the lists? for food list(\"f\"), for Total Price(\"p\"): ")


if userView.lower() == 'f':
    print("-------YOUR CART------")
    print(foods)
elif userView.lower() == 'p':
    print("-------TOTAL PRICE------")
    for x in prices:
        total += x

print(total)

print("Thank you for using this machine")

