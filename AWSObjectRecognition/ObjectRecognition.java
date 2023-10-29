package cloud.aws.ec2;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
import java.util.List;
import java.util.ListIterator; 
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
public class ObjectRecognition 
{
    public static void main( String[] args )
    {
    	String bucketName = "njit-cs-643";
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        CreateQueueRequest request = CreateQueueRequest.builder()
                .queueName("imagesqs")
                .build();
            sqsClient.createQueue(request);
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();
 
            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName("imagesqs")
                    .build();
            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext(); ) {
                S3Object myValue = (S3Object) iterVals.next();

                
                software.amazon.awssdk.services.rekognition.model.S3Object s3Object = software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                            .bucket(bucketName)
                            .name(myValue.key())
                            .build() ;
 
                    Image myImage = Image.builder()
                            .s3Object(s3Object)
                            .build();
 
                    DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                            .image(myImage)
                            .maxLabels(10)
                            .build();
 
                    DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
                    List<Label> labels = labelsResponse.labels();
                    

                    for (Label label: labels) {
                        if(label.name().equalsIgnoreCase("Car") && label.confidence()>90) {
                        CreateQueueResponse createResult = sqsClient.createQueue(request);
                        
                        
     
                        
                        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(myValue.key())
                            .delaySeconds(5)
                            .build();
                        System.out.println(myValue.key()+" is car and has been sent to sqs");
                        sqsClient.sendMessage(sendMsgRequest);
                    }}
                    
                    
 
             }
            Thread.sleep(6000);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody("-1")
                    .delaySeconds(5)
                    .build();
            sqsClient.sendMessage(sendMsgRequest);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
          
}
