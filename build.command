(rm build/libs/rcam-coordinator-all-1.0.jar); gradle fatJar && java -javaagent:./spring-instrument-4.1.5.RELEASE.jar -jar ./build/libs/rcam-coordinator-all-1.0.jar 
