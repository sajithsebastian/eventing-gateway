resource "aws_msk_cluster" "msk_cluster_a" {
  cluster_name           = "${var.cluster_name}-a"
  kafka_version          = "3.6.0"
  number_of_broker_nodes = length(var.subnet_ids)

  broker_node_group_info {
    instance_type   = "kafka.m5.large"
    client_subnets  = var.subnet_ids
    security_groups = var.security_group_ids
  }
}

resource "aws_msk_cluster" "msk_cluster_b" {
  cluster_name           = "${var.cluster_name}-b"
  kafka_version          = "3.6.0"
  number_of_broker_nodes = length(var.subnet_ids)

  broker_node_group_info {
    instance_type   = "kafka.m5.large"
    client_subnets  = var.subnet_ids
    security_groups = var.security_group_ids
  }
}

output "msk_bootstrap_brokers_a" {
  value = aws_msk_cluster.msk_cluster_a.bootstrap_brokers_sasl_iam
}

output "msk_bootstrap_brokers_b" {
  value = aws_msk_cluster.msk_cluster_b.bootstrap_brokers_sasl_iam
}
