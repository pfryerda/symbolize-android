#swap.py
import sys
import re
import xml.etree.ElementTree

def write_xml(tree, file):
	 tree.write(file, xml_declaration=True, encoding='utf-8', method="xml")
	 print ('Wrote to ' + file)

XML_BASE_URL = './Symbolize/app/src/main/res/xml/'
HINTS_FILE = './Symbolize/app/src/main/res/values/hints.xml'
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

# Parse command line args
# -----------------------

world_1, level_1 = sys.argv[1].split('-')
world_2, level_2 = sys.argv[2].split('-')
file_1 = XML_BASE_URL + 'level_' + world_1 + '_' + level_1 + '.xml'
file_2 = XML_BASE_URL + 'level_' + world_2 + '_' + level_2 + '.xml'


# Swap level (xml) contents
# -------------------------

tree_1 = xml.etree.ElementTree.parse(file_1)
tree_2 = xml.etree.ElementTree.parse(file_2)
root_1 = tree_1.getroot()
root_2 = tree_2.getroot()

temp = root_1[0]
root_1[0] = root_2[0]
root_2[0] = temp

write_xml(tree_2, file_1)
write_xml(tree_1, file_2)


# Edit hints.xml
# --------------

world_1, level_1, world_2, level_2 = int(world_1), int(level_1), int(world_2), int(level_2)

hints_tree = xml.etree.ElementTree.parse(HINTS_FILE)
hints_root = hints_tree.getroot()

temp = hints_root[world_1 - 1][level_1]
hints_root[world_1 - 1][level_1] = hints_root[world_2 - 1][level_2]
hints_root[world_2 - 1][level_2] = temp

temp = hints_root[world_1 - 1][level_1].attrib['level']
hints_root[world_1 - 1][level_1].attrib['level'] = hints_root[world_2 - 1][level_2].attrib['level']
hints_root[world_2 - 1][level_2].attrib['level'] = temp

write_xml(hints_tree, HINTS_FILE)
