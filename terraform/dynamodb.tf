resource "aws_dynamodb_table" "topic_mappings" {
  name           = "EventingGatewayTopicMappings"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "topicName"

  attribute {
    name = "topicName"
    type = "S"
  }
}
