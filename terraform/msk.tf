resource "aws_msk_cluster" "msk_cluster_a" {
  cluster_name           = "${var.cluster_name}-a"
  kafka_version          = "3.6.0"
  number_of_broker_nodes = 3

  broker_node_group_info {
    instance_type   = "kafka.m5.large"
    client_subnets  = ["subnet-12345678", "subnet-87654321", "subnet-11223344"]
    security_groups = ["sg-12345678"]
  }
}

resource "aws_msk_cluster" "msk_cluster_b" {
  cluster_name           = "${var.cluster_name}-b"
  kafka_version          = "3.6.0"
  number_of_broker_nodes = 3

  broker_node_group_info {
    instance_type   = "kafka.m5.large"
    client_subnets  = ["subnet-12345678", "subnet-87654321", "subnet-11223344"]
    security_groups = ["sg-12345678"]
  }
}

output "msk_bootstrap_brokers_a" {
  value = aws_msk_cluster.msk_cluster_a.bootstrap_brokers_sasl_iam
}

output "msk_bootstrap_brokers_b" {
  value = aws_msk_cluster.msk_cluster_b.bootstrap_brokers_sasl_iam
}
