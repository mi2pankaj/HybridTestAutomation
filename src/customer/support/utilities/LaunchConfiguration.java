package customer.support.utilities;

public class LaunchConfiguration {

	public static void main(String[] args) {

		String action = args[0].trim();

		if(action.trim().equalsIgnoreCase("CustomerAccountDeletion"))
		{
			try
			{
				String emailId = args[1].trim();

				if(!emailId.isEmpty())
				{
					String encodedEmailId=CustomerSupport.encodeEmailId(emailId);
					CustomerSupport.deleteCustomerAccount(encodedEmailId);
				}
				else
				{
					System.out.println("********* No Email Id Was Supplied ******* ");
				}
			}catch (java.lang.ArrayIndexOutOfBoundsException e) {
				System.out.println("********* No Email Id Was Supplied ******* ");
			}	
		}

		else if(action.trim().equalsIgnoreCase("MergeMobileEmailAccount"))
		{
			try
			{
				String customerEmail = args[1].trim();
				String mobileNumber = args[2].trim();

				CustomerSupport.mergeMobileEmailAccount(customerEmail, mobileNumber);

			}catch (java.lang.ArrayIndexOutOfBoundsException e) {
				System.out.println("********* Please Supply Both Email & Mobile Number ******* ");
			}
		}

		else if(action.trim().equalsIgnoreCase("ManageMobileWalletAmount"))
		{
			try
			{
				String mobileNumber = args[1].trim();
				String amount = args[2].trim();
				String flagBulkCredit_Or_BulkDebit = args[3];

				CustomerSupport.walletBulkCredit_BulkDebit(mobileNumber, amount, "Added By Lenskart", flagBulkCredit_Or_BulkDebit);

			}catch (java.lang.ArrayIndexOutOfBoundsException e) {
				System.out.println("********* Please Supply All Values Mobile Number, Amount ******* ");
			}
		}else if(action.trim().equalsIgnoreCase("ManageLKPlusWalletAmount")){
			try
			{
				String mobileNumber = args[1].trim();
				String amount = args[2].trim();
				String flagBulkCredit_Or_BulkDebit = args[3];
				String wallet_type =args[4];
				String order_id = args[5];

				CustomerSupport.walletLKPlusBulkCredit_BulkDebit(mobileNumber, amount, "Added By Lenskart", flagBulkCredit_Or_BulkDebit, wallet_type, order_id);

			}catch (java.lang.ArrayIndexOutOfBoundsException e) {
				System.out.println("********* Please Supply All Values Mobile Number, Amount ******* ");
			}
		}else if(action.trim().equalsIgnoreCase("ManageLoyalty")){
			try{
				String order_ids = args[1].trim();
				String addOrRemoveLoyalty = args[2].trim();
				
				CustomerSupport.addOrRemoveLoyalty(order_ids, addOrRemoveLoyalty);
				
			}catch(java.lang.ArrayIndexOutOfBoundsException e){
				System.out.println("********* Please Supply All Values order_ids, addLoyalty parameter ******* ");
			}
		}

	}

}
