# Setup Guide (Ubuntu 22.04)

## 1. Install tools
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven git curl tomcat9 tomcat9-admin
java -version && mvn -version && git --version
```

## 2. Jenkins
```bash
sudo mkdir -p /etc/apt/keyrings
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list
sudo apt update && sudo apt install -y jenkins
sudo systemctl enable --now jenkins
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```
Open `http://<host>:8080`, install suggested plugins. Then in *Manage Jenkins > Tools* add:
- JDK named `JDK17` (JAVA_HOME `/usr/lib/jvm/java-17-openjdk-amd64`)
- Maven named `Maven3` (install automatically)

## 3. Avoid port conflict: move Tomcat to 8081
```bash
sudo sed -i 's/port="8080"/port="8081"/' /etc/tomcat9/server.xml
sudo systemctl restart tomcat9
ss -tulpn | grep -E '8080|8081'
```

## 4. Permissions so Jenkins can deploy
```bash
sudo usermod -aG tomcat jenkins
sudo chmod g+w /var/lib/tomcat9/webapps
sudo mkdir -p /var/lib/jenkins/war-backups && sudo chown jenkins:jenkins /var/lib/jenkins/war-backups
sudo systemctl restart jenkins
```
(If your Tomcat group is not `tomcat`, check with `ls -ld /var/lib/tomcat9/webapps`.)

## 5. Git
```bash
git init && git checkout -b main
git add . && git commit -m "feat: initial feedback portal with maven war"
git remote add origin https://github.com/<you>/devops-ci-cd-project.git
git push -u origin main
git checkout -b dev && git push -u origin dev
git checkout -b feature/feedback-form
```

## 6. Jenkins job
New Item → *Multibranch Pipeline* (or Pipeline) → Git repo URL → script path `Jenkinsfile` → Save → Build Now.
Optional: GitHub webhook `http://<host>:8080/github-webhook/`. Store any credentials in Jenkins Credentials.

## 7. Verify
```bash
curl -i http://localhost:8081/feedback-portal/health
tail -n 50 /var/log/tomcat9/catalina.out
```
