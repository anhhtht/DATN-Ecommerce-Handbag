import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import CartCard from '../CartCard';
import './style.scss';
import { toast, ToastContainer } from 'react-toastify';
import Button from '~/pages/Button';
import apiCart from '~/api/user/apiCart';
import apiUpdateCartItems from '~/api/user/apiUPdateCartItems';
import apiRemoveCartItems from '~/api/user/apiRemoveCartItems';
import { useCart } from '~/api/user/CartContext';

export default function CartList() {
    const { setCartItems } = useCart();
    const [products, setProducts] = useState([]);
    const navigate = useNavigate();
    const checksessionStorage = () => {
        if (!sessionStorage.getItem('jwt')) {
            navigate('/login');

            return false;
        }
        return true;
    };
    const fetchCarts = async () => {
        if (!checksessionStorage()) {
            return;
        }
        try {
            const response = await apiCart.getAllCart();
            setProducts(response.data);
        } catch (error) {
            console.log(error);
        }
    }; // API cart
    useEffect(() => {
        fetchCarts();
    }, []);

    const handleQuantityChange = async (productId, newQuantity) => {
        const product = products.cartItems.find((item) => item.id === productId);

        if (!product) {
            console.error(`Product with ID ${productId} not found.`);
            return;
        }

        const formData = {
            quantity: newQuantity,
            size: product.size,
        };
        console.log(formData);
        try {
            const response = await apiUpdateCartItems.putUpdateCartItems(productId, formData);
            console.log(response);
            if (response) {
                fetchCarts();
            } else {
            }
        } catch (error) {
            console.log(error);
        }
    };

    const handleIncreaseQuantity = (productId) => {
        const product = products.cartItems.find((p) => p.id === productId);
        const currentQuantity = product ? product.quantity : 0;
        const newQuantity = currentQuantity + 1;
        handleQuantityChange(productId, newQuantity);
    };

    const handleDecreaseQuantity = (productId) => {
        const product = products.cartItems.find((p) => p.id === productId);
        const currentQuantity = product ? product.quantity : 0;
        const newQuantity = currentQuantity - 1;
        handleQuantityChange(productId, newQuantity);
    };

    const handleDeleteProduct = async (productId) => {
        try {
            const response = await apiRemoveCartItems.delRemoveCartItems(productId);
            if (response) {
                fetchCarts();
                // toast.success('Product deletion successful');
                setCartItems((prevCartItems) => prevCartItems.filter((item) => item.id !== productId));
            } else {
            }
        } catch (error) {
            toast.error(error.message);
        }
    };

    const handleDeleteAllProducts = async () => {
        try {
            for (const product of products.cartItems) {
                await apiRemoveCartItems.delRemoveCartItems(product.id);
            }
            fetchCarts();
            window.location.reload();
            toast.success('Delete all products successfully');
            setCartItems([]);
        } catch (error) {
            toast.error('All products removed');
        }
    };
    const handleBuyNow = () => {
        if (products.cartItems.length === 0) {
            toast.warning('There are no products in the cart. Please add products before payment.');
        } else {
            // Chuyển đến trang thanh toán
            navigate('/pay?step=1');
        }
    };
    const handleEditProduct = (productId, item) => {
        navigate(`/product/${productId}`);
        setTimeout(() => {
            handleDeleteProduct(item);
        }, 2000);
    };
    return (
        <>
            {products?.cartItems?.length > 0 ? (
                <div className="cart container-layout">
                    <ToastContainer />
                    <div className="cart-operation">
                        <Link to="/product" className="cart-operation-link">
                            <button className="cart-operation-add">Add New Products</button>
                        </Link>
                    </div>
                    <div className="cartRow">
                        <div className="cartRow-product">Product</div>
                        <div className="cartRow-price">Unit price</div>
                        <div className="cartRow-priceSale">Sale price</div>
                        <div className="cartRow-quantity">Quantity</div>
                        <div className="cartRow-money">Total</div>
                        <div className="cartRow-operation">Operation</div>
                    </div>

                    {/* Danh sách sản phẩm  */}
                    {products?.cartItems?.length > 0 &&
                        products?.cartItems
                            .sort((a, b) => b.timestamp - a.timestamp)
                            ?.map((product) => {
                                return (
                                    <CartCard
                                        key={product?.id}
                                        product={product}
                                        onDelete={() => handleDeleteProduct(product.id)}
                                        onEditProduct={() => handleEditProduct(product.product.id, product.id)}
                                        onIncreaseQuantity={() => handleIncreaseQuantity(product.id)}
                                        onDeCreaseQuantity={() => handleDecreaseQuantity(product.id)}
                                    />
                                );
                            })}
                </div>
            ) : (
                <div style={{ textAlign: 'center' }}>
                    <img
                        src="https://dl.dropboxusercontent.com/scl/fi/3smlpv3wqyin540i1eg5f/parcel-alert-is-a-flat-conceptual-icon-with-download-facility-vector.jpg?rlkey=ufg7jxctny29zrfscxeq0h2w3&dl=0"
                        alt="No products"
                        style={{ display: 'block', margin: 'auto', width: '30%', height: 'auto' }}
                    />
                </div>
            )}
            <div className="payment">
                <div className="payment-voucher">
                    <i className="fa fa-ticket" aria-hidden="true" />
                    <span>Your voucher</span>
                </div>
                <div className="payment-detail">
                    <button className="payment-detail-btnall">Select all</button>
                    <button className="payment-detail-btndelete" onClick={handleDeleteAllProducts}>
                        Delete
                    </button>
                    <div className="payment-content">
                        <label>The Total Amount</label>
                        <label>{products?.totalDiscountedPrice + ' '}VND</label>
                    </div>
                </div>
                <div className="payment-btn">
                    <Button text="Buy Now" onClick={handleBuyNow} className={'payment-btn-buy'}>
                        Buy Now
                    </Button>
                </div>
            </div>
        </>
    );
}