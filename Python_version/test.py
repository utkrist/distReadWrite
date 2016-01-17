import uuid
id1 = uuid.uuid5(uuid.NAMESPACE_DNS, 'localhost:2000')
id2 = uuid.uuid5(uuid.NAMESPACE_DNS, 'localhost:2001')
id3 = uuid.uuid5(uuid.NAMESPACE_DNS, 'localhost:2002')

print id1.int
print id2.int
print id3.int
