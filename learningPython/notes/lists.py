#[] = ordered and changable, Allows duplicates
# list = used to store multiple items in a single variable

food = ['pizza', 'hamburger', 'hotdog', 'sphagetti', 'pudding']

#each item in a list is referred to as an element

#food[0] = "chowmein"

#print(food)  #output: chowmein

#reversed = food.reverse() #print(reversed)  #output: pudding, sphagetti, hotdog, hamburger, pizza
#food.append('ice-cream') #adds item to end of list
#food.remove('hotdog') #removes specific item from list
#food.pop()  #removes last item in list
#food.insert(0, 'cake')  #inserts item at index 0
#food.sort() #sorts list in alphabetical order
#food.clear()  #removes all items in list


#for x in food:
#    print(x, end=', ') 




# 2D LISTS(Multi-dimensional) = LISTS WITHIN LISTS

drinks = ['coffee', 'soda', 'tea']
dinner = ['pizza', 'hamburger', 'hotdog']
dessert = ['cake', 'ice-cream']

food = [drinks, dinner, dessert]
print(food[0][0])
#first set of brackets refer to the list
#second set of brackets refer to the item in that list
