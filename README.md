# Voice-Notebook
Voice commands driven web app capable of creating notes in real time. 

To start server app you need to:
1. Setup postgresql database and connection in application.properties
2. Install mvn dependencies
3. Run app with configuratuon to create or update database schema (eg. spring.jpa.hibernate.ddl-auto=create)

Web angular app should start after just npm install.

To test external asr services you need to:
1. For google provide key json file and path to it in application.properties: google.key-path
2. For azure provide key and region in application.properties: azure.key, azure.region
3. For revAi provide token in application.properties: revai.token
4. For speechtext provide token in application.properties: speechtext.key
5. For speechmatics provide token in application.properties: speechmatics.token

Services form point 4 and 5 don't include real time speech to text.

Scripts folder contains script with python fastApi for calculation of WER and LEVENSTHEIN metrics of comapred texts.
