#!/bin/bash

echo "-----------------------------------"
echo $"Virginia, 4"
scp -i nmorin-keypair -r code nmorin@52.5.12.102:

echo $"California, 4"
scp -i nmorin-keypair -r code nmorin@52.8.60.130:

echo $"Oregon, 4"
scp -i nmorin-keypair -r code nmorin@52.11.50.220:

echo $"Ireland, 4"
scp -i nmorin-keypair -r code nmorin@52.17.33.248:

echo $"Sao Paulo, 4"
scp -i nmorin-keypair -r code nmorin@54.94.245.199:

echo $"Tokyo, 4"
scp -i nmorin-keypair -r code nmorin@52.68.45.217:

echo $"Sydney, 4"
scp -i nmorin-keypair -r code nmorin@52.64.47.128:

echo $"Singapore, 4"
scp -i nmorin-keypair -r code nmorin@52.74.155.86:

