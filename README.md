# AWS-Image-Recognition-Pipeline

An image recognition pipeline in AWS, using two parallel EC2 instances, S3, SQS, and Rekognition.

**Goal**: The purpose of this individual assignment is to learn how to use the Amazon AWS cloud platform and how to develop an AWS application that uses existing cloud services. Specifically, you will learn:
1. How to create VMs (EC2 instances) in the cloud.
2. How to use cloud storage (S3) in your applications.
3. How to communicate between VMs using a queue service (SQS).
4. How to program distributed applications in Java on Linux VMs in the cloud, and
5. How to use a machine learning service (AWS Rekognition) in the cloud.

**Description**: You have to build an image recognition pipeline in AWS, using two EC2 instances, S3, SQS, and Rekognition. The assignment must be done in Java on Amazon Linux VMs. For the rest of the description, you should refer to the figure below:

![image](https://github.com/SATHYARUPAN/Owner-avatar-AWS-Object-Text-Recognition-Pipeline-/assets/53247339/45f3f6c3-8796-4e51-ae54-84b6d40b4d3d)

Your have to create 2 EC2 instances (EC2 A and B in the figure), with Amazon Linux AMI, that will work in parallel. Each instance will run a Java application. Instance A will read 10 images from an S3 bucket that we created (https://njit-cs-643.s3.us-east-1.amazonaws.com) and perform object detection in the images. When a car is detected using Rekognition, with confidence higher than 90%, the index of that image (e.g., 2.jpg) is stored in SQS. Instance B reads indexes of images from SQS as soon as these indexes become available in the queue, and performs text recognition on these images (i.e., downloads them from S3 one by one and uses Rekognition for text recognition). Note that the two instances work in parallel: for example, instance A is processing image 3, while instance B is processing image 1 that was recognized as a car by instance A. When instance A terminates its image processing, it adds index -1 to the queue to signal to instance B that no more indexes will come. When instance B finishes, it prints to a file, in its associated EBS, the indexes of the images that have both cars and text, and also prints the actual text in each image next to its index.

# Approached Solution

1) Login to AWS Academy Account.
2) Access Learner Lab in modules.
3) Launch the AWS lab and console. In the lab environment, find and copy the following credentials:
        aws_access_key_id<br>
        aws_secret_access_key<br>
        aws_session_token<br>
3) Update AWS Credentials: Paste these credentials into your AWS credentials file located at ~/.aws/credentials. Your config file should also contain the region (e.g., us-east-1 for N. Virginia) and output format (e.g., JSON).
4)Download SSH Key: Download the labsuser.pem file from the SSH Key section. 
5)Create EC2 Instances: After starting your lab, open the AWS Management Console. Search for "EC2" under Services.
