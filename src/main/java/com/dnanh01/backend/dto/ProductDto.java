package com.dnanh01.backend.dto;

import java.time.LocalDateTime;

public class ProductDto {

    private Long id;
    private String title;
    private String description;
    private int price;
    private int discountedPrice;
    private int discountPercent;
    private int quantity;
    private String imageUrl;
    private LocalDateTime createAt;
    private Long brandId;
    private String brandName;

    

    public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public int getPrice() {
		return price;
	}



	public void setPrice(int price) {
		this.price = price;
	}



	public int getDiscountedPrice() {
		return discountedPrice;
	}



	public void setDiscountedPrice(int discountedPrice) {
		this.discountedPrice = discountedPrice;
	}



	public int getDiscountPercent() {
		return discountPercent;
	}



	public void setDiscountPercent(int discountPercent) {
		this.discountPercent = discountPercent;
	}



	public int getQuantity() {
		return quantity;
	}



	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}



	public String getImageUrl() {
		return imageUrl;
	}



	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}



	public LocalDateTime getCreateAt() {
		return createAt;
	}



	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}



	public Long getBrandId() {
		return brandId;
	}



	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}



	public String getBrandName() {
		return brandName;
	}



	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}



	// Constructor có tất cả các tham số
    public ProductDto(Long id, String title, String description, int price, int discountedPrice, 
                      int discountPercent, int quantity, String imageUrl, LocalDateTime createAt,
                      Long brandId, String brandName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.discountPercent = discountPercent;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.createAt = createAt;
        this.brandId = brandId;
        this.brandName = brandName;
    }
}
