package lenskart.api.productApiMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"frameDetails", "male180", "female180", "prescriptionType", "colorOptions", "relatedItems", "crossSells", "specifications","thumbnailImage"
	,"deliveryOptions", "classification", "description", "imageResolutions", "review", "status", "isBulkAvailable", "storeId", "imageUrlsDetail",
	"mostViewed", "bogoEnabled", "offer", "wishlistCount", "purchaseCount", "subCategories", "lastPurchaseInfo", "breadcrumb", "offerBanner","totalNoOfRatings",
	"numberOfReviews"})
public class Product_child {

	private String id ;
	
	private boolean isTryNowAvailable;
	
	private long qty;
	
	private boolean isDittoEnabled;
	
	private List<String> imageUrls;
	
	private String url;
	
	private String type;
	
	private String sku;

	private String fullName;
	
	private boolean isHecButton;
	
	private String brandName;
	private String modelName;
	private boolean isTbyb;
	
	private String offerImage;
	
	private int avgRating;
	
	private String seoTitle;
	private String seoMetaDescription;
	private String seoMetaKeywords;
	private String seoMetaCanonical;
	private String seoMetaAlternate;
	
	private List<Map<String, String>> prices= new ArrayList<>();
	
	private ProductAPISubscription subscription;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isisTryNowAvailable() {
		return isTryNowAvailable;
	}

	public void setisTryNowAvailable(boolean isTryNowAvailable) {
		this.isTryNowAvailable = isTryNowAvailable;
	}

	public long getQty() {
		return qty;
	}

	public void setQty(long qty) {
		this.qty = qty;
	}

	public boolean isisDittoEnabled() {
		return isDittoEnabled;
	}

	public void setisDittoEnabled(boolean isDittoEnabled) {
		this.isDittoEnabled = isDittoEnabled;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isisHecButton() {
		return isHecButton;
	}

	public void setisHecButton(boolean isHecButton) {
		this.isHecButton = isHecButton;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public boolean isisTbyb() {
		return isTbyb;
	}

	public void setisTbyb(boolean isTbyb) {
		this.isTbyb = isTbyb;
	}

	public String getOfferImage() {
		return offerImage;
	}

	public void setOfferImage(String offerImage) {
		this.offerImage = offerImage;
	}

	public int getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(int avgRating) {
		this.avgRating = avgRating;
	}

	public String getSeoTitle() {
		return seoTitle;
	}

	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}

	public String getSeoMetaDescription() {
		return seoMetaDescription;
	}

	public void setSeoMetaDescription(String seoMetaDescription) {
		this.seoMetaDescription = seoMetaDescription;
	}

	public String getSeoMetaKeywords() {
		return seoMetaKeywords;
	}

	public void setSeoMetaKeywords(String seoMetaKeywords) {
		this.seoMetaKeywords = seoMetaKeywords;
	}

	public String getSeoMetaCanonical() {
		return seoMetaCanonical;
	}

	public void setSeoMetaCanonical(String seoMetaCanonical) {
		this.seoMetaCanonical = seoMetaCanonical;
	}

	public String getSeoMetaAlternate() {
		return seoMetaAlternate;
	}

	public void setSeoMetaAlternate(String seoMetaAlternate) {
		this.seoMetaAlternate = seoMetaAlternate;
	}

	public ProductAPISubscription getSubscription() {
		return subscription;
	}

	public void setSubscription(ProductAPISubscription subscription) {
		this.subscription = subscription;
	}

	public List<Map<String, String>> getPrices() {
		return prices;
	}

	public void setPrices(List<Map<String, String>> prices) {
		this.prices = prices;
	}
	
	
}
