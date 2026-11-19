## 1.2.1

- Fix `cancelSubscription` and `refundSubscription` sending PUT requests without a body (the backend answered 422); the JSON body is now sent for every request that has one
- Fix `chargeUser` using PUT on a POST-only route (405); it now sends POST, with `price_id` as a number
- Fix successful cancel/charge being reported as failures: `success` is now accepted as `true`, `1`, `"1"` or `"true"`
- Failure results now carry the backend reason (`error_msg`, then `message`), including for non-2xx responses such as a 403 for an API key without the required scope, instead of "Empty response"
- Send `Accept: application/json`
- Replace `String.isBlank()` (a Java 11 API that older Android versions do not have) with a compatible check

## 1.2.0

- Add `setApphudProfileID` and `setSuperwallProfileID` (sync web subscriptions to Apphud / Superwall)

## 1.1.0

- Send `device_model` header for identify fingerprinting (`Build.MODEL`)

## 1.0.0

- Initial release
