# Icripto POS
Multi Server BTC Payment Terminal.

Icripto POS is a payment terminal that allow vendors to receive payments in bitcoin.
The app does not connect to any centralized backend. 
It only connects to the server the merchant chooses to (BTCPay, LNBits or his/her account at buda.com)

The terminal currently supports:
- Connection to BTCPay Server for accepting payments in bitcoin (on chain and lightning / NFC, plus altcoins via plugins). This uses the payment button for simplicity. It allows servers located both over clearnet and over Tor network.
- Connection to LNBits Server for accepting payments in bitcoin (on chain and lightning). For now this works with servers on clearnet only.
- Connection to buda.com for accepting payments in bitcoin (lightning only).

Payments to onchain addresses are usually detected/recognised by evaluating the balance of the given address. Therefore, it is essential that the onchain root public key (Xpub, Zpub) used to configure the onchain payments, only has empty addresses (addresses with zero balance) when setting it up, and make sure you only use that root public key for this purpose. 
For example, only use the <<M84/0/0/4>> descriptor for the btcpay server onchain wallet or the lnbits onchain wallet.

TODOs:
- Enabling payments to Buda.com with bitcoin on chain. To be analized since Buda does not disclose how they manage the root public keys.
- Enabling connections to LNBits and BTCPay Servers hosted behind onion addresses. I need a bit more knowledge to use http requests (POST) via socks protocol in order to enable this, as well as enable NFC interaction over the Tor network.

Donations via bitcoin lightning:
LNAddress: felipe@btcpay.icripto.cl


