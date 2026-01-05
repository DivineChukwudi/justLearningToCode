#   set = collection which is unordered, unindexed. No duplicate values

utensils = {'fork', 'spoon', 'knife'}
dishes = {'bowl','plate','cup','knife'}

#utensils.add('napkin')
#utensils.remove('fork')
#utensils.clear()  #removes all items in set
#utensils.update(dishes)
#dinnerTable= utensils.union(dishes)
#print(dinnerTable)

#print(utensils.difference(dishes))  #items in utensils but not in dishes
print(utensils.intersection(dishes)) #items in both utensils and dishes

#for x in utensils:
#    print(x)
    
