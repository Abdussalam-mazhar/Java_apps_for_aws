pipeline {
  agent any
  tools { 
        maven 'Maven_3_5_2'  
    }
   stages{
    stage('CompileandRunSonarAnalysis') {
            steps {	
		sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=salam_salam -Dsonar.organization=salam -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=75d881a4f045509b9e02e232c60a4f8e42ced785'
			}
        } 
  }
}
