text = """
Address		0.8.0
Ancient		0.8.0
Animal		0.8.0
App		0.8.0
Appliance		1.0.0
Aqua Teen Hunger Force		0.8.0
Artist		0.8.0
Avatar		0.8.0
Aviation		0.8.0
AWS		1.3.0
Babylon 5		0.9.0
Back To The Future		0.8.0
Barcode		0.9.0
Basketball		0.8.0
Battlefield 1		1.4.0
Beer		0.8.0
Blood Type		1.4.0
Bojack Horseman		0.8.0
Book		0.8.0
Bool		0.8.0
Bossa Nova		1.0.0
BreakingBad		1.0.0
Brooklyn Nine Nine		1.3.0
Business		0.8.0
CNPJ	Cadastro Nacional da Pessoa Jurídica in Portuguese, or 'National Registry of Legal Entities'	1.1.0
CPF	Brazilian individual taxpayer registry identification	0.8.0
Camera		1.4.0
Cat		0.8.0
Chuck Norris		0.8.0
Code		0.8.0
Coin		0.8.0
Color		0.8.0
Commerce		0.8.0
Company		0.8.0
Crypto Coin		1.3.0
Date And Time		0.8.0
Demographic		0.8.0
Dessert		0.9.0
Device		1.4.0
Disease		0.8.0
Dog		0.8.0
Domain		0.9.0
Dragon Ball		0.8.0
Dune		0.8.0
Durations		0.9.0
Educator		0.8.0
Elden Ring		1.4.0
Electrical Components		1.4.0
England FootBall		0.9.0
Esports		0.8.0
File		0.8.0
Finance		0.8.0
Food		0.8.0
Friends		0.8.0
Funny Name		0.8.0
Game Of Thrones		0.8.0
Gender		0.8.0
Grateful Dead		1.4.0
Hacker		0.8.0
Harry Potter		0.8.0
Hashing		0.8.0
Heartstone		0.9.0
Hey Arnold		1.4.0
Hipster		0.8.0
Hitchhikers Guide To The Galaxy		0.8.0
Hobbit		0.8.0
Hobby		1.3.0
Horse		1.3.0
How I Met Your Mother		0.8.0
IdNumber		0.8.0
Internet		0.8.0
Job		0.8.0
Kaamelott		0.8.0
K Pop		1.3.0
League Of Legends		0.8.0
Lebowski		0.8.0
Lord Of The Rings		0.8.0
Lorem		0.8.0
Matz		0.8.0
Marketing		1.2.0
Medical		0.8.0
Military		1.2.0
Minecraft		0.9.0
Mood		0.9.0
Mountaineering		1.4.0
Mountains	Support for mountains and mountain ranges	1.1.0
Music		0.8.0
Name		0.8.0
Nation		0.8.0
Nato Phonetic Alphabet		1.2.0
Number		0.8.0
Oscar Movie		1.4.0
Options		0.8.0
Overwatch		0.8.0
Passport		0.9.0
Phone Number		0.8.0
Photography		0.8.0
Pokemon		0.8.0
Princess Bride		0.8.0
Relationship Terms		0.8.0
Resident Evil		0.9.0
Restaurant		1.2.0
Rick And Morty		0.8.0
Robin		0.8.0
RockBand		0.8.0
RuPaul's Drag Race		1.0.0
Seinfeld		1.4.0
Shakespeare		0.8.0
Sip		0.8.0
Slack Emoji		0.8.0
Soul Knight		1.4.0
Space		0.8.0
StarCraft		0.8.0
Star Trek		0.8.0
Stock		0.8.0
Superhero		0.8.0
Subscription		1.3.0
Super Mario		1.3.0
Tea		1.4.0
Team		0.8.0
The IT Crowd		1.2.0
Time		1.4.0
Touhou		0.9.0
Tron		1.4.0
Twin Peaks		0.8.0
Twitter		0.9.0
University		0.8.0
Vehicle		0.9.0
Volleyball		1.3.0
Weather		0.8.0
Witcher		0.8.0
Yoda		0.8.0
Zelda
"""

import json
ls = []

for line in text.strip().splitlines():
    provider = line.split()[0].strip()
    ls.append(provider)

print(json.dumps(ls))

