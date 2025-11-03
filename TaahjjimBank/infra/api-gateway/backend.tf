terraform {
  required_version = ">= 1.5"

  backend "s3" {
    bucket  = "ip4-pontuacao-tf-state"
    key     = "hom/terraform.tfstate"
    region  = "sa-east-1"
    encrypt = true
  }
}
