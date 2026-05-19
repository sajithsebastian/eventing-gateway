variable "region" {
  type        = string
  description = "The AWS region to deploy into"
  default     = "us-east-1"
}

variable "cluster_name" {
  type        = string
  description = "Base name for the MSK clusters"
  default     = "eventing-gateway-msk"
}

variable "vpc_id" {
  type        = string
  description = "The VPC ID where MSK should be deployed"
}

variable "subnet_ids" {
  type        = list(string)
  description = "List of subnet IDs for MSK broker nodes"
}

variable "security_group_ids" {
  type        = list(string)
  description = "List of security group IDs for MSK"
}
