import sys
import pandas as pd
import requests

filepath = sys.argv[1]

sheets = pd.read_excel(filepath)

for row in sheet.itertuples():
   name = ' '.join(filter(lambda s: s != '*' and not s.endswith('.'), row[1].split()))
   requests.get(f'http://localhost:8080/student/new/',params={
   'apiKey':'testlmao',
   'name':name,
   'studentId':int(row[2]),
   'graduatingYear':2020+12-int(row[3].split()[2])
   })
