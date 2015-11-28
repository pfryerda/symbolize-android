#swap.py
import sys
import re
import xml.etree.ElementTree

# Variable definitions
# --------------------

XML_BASE_URL = './test_1/'
argc = len(sys.argv) - 1


# Error checking
# --------------

if argc != 2:
	print ('swap.py expects 2 arguments given ' + str(argc))
	sys.exit(1)

if not re.match('^[1-5]-([1-9]|10)$', sys.argv[1]):
	print ('swap.py expected argument 1 of the from #-#, given ' + str(sys.argv[1]))
	sys.exit(1)

if not re.match('^[1-5]-([1-9]|10)$', sys.argv[2]):
	print ('swap.py expected argument 2 of the from #-#, given ' + str(sys.argv[2]))
	sys.exit(1)

if sys.argv[1] == sys.argv[2]:
	print ('swap.py expects two differnt arguments.')
	sys.exit(1)


# Swap xml contents
# -----------------

world_1, level_1 = sys.argv[1].split('-')
world_2, level_2 = sys.argv[2].split('-')
file_1 = XML_BASE_URL + 'level_' + str(world_1) + '_' + str(level_1) + '.xml'
file_2 = XML_BASE_URL + 'level_' + str(world_2) + '_' + str(level_2) + '.xml'

tree_1 = xml.etree.ElementTree.parse(file_1)
tree_2 = xml.etree.ElementTree.parse(file_2)
root_1 = tree_1.getroot()
root_2 = tree_2.getroot()

temp = root_1[0]
root_1[0] = root_2[0]
root_2[0] = temp

print ('Writing to ' + file_1)
tree_2.write(file_1)
print ('Writing to ' + file_2)
tree_1.write(file_2)
