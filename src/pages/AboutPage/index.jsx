import Header from '../../layouts/UserDefaultLayout/Header';
import './style.scss';
import { useEffect, useState } from 'react';
import { toast, ToastContainer } from 'react-toastify';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useCart } from '~/api/user/CartContext';
import Button from '../Button';
import apiAddItem from '~/api/user/apiAddItem';
import apiProductDetail from '~/api/admin/apiProductDetail';
import CommentCard from '~/components/CommentCard';

export default function AboutPage({ quantity = 1 }) {
    const navigate = useNavigate();
    const [productDetail, setProductDetail] = useState([]);
    const { cartItems } = useCart();
    const { updateCartItems } = useCart();
    const [selectedColor, setSelectedColor] = useState('');
    const [quantityDefault, setQuantityDefault] = useState(quantity);
    const [isLoading, setIsLoading] = useState(true);
    const { id } = useParams();
    const [selectedColorQuantity, setSelectedColorQuantity] = useState(null);
    const [maxQuantity, setMaxQuantity] = useState(1);

    const handleColorClick = (color) => {
        setSelectedColor(color.name);
        setSelectedColorQuantity(color.quantity);
        setQuantityDefault(1);
    };

    const handleAddToCart = async (productId) => {
        if (!selectedColor) {
            toast.warning('Please select color before adding to cart');
            return;
        }
        const formData = {
            productId,
            quantity: quantityDefault,
            color: selectedColor,
        };
        try {
            setIsLoading(true);

            const response = await apiAddItem.putAddItem(formData);
            toast.success('Added product to cart successfully');
            updateCartItems();
            console.log(response);
        } catch (error) {
            console.error('Add to Cart Error:', error);
            toast.error('You need to log in to use this function');
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        const fetchProductDetail = async () => {
            try {
                setIsLoading(true);

                const response = await apiProductDetail.getProductDetail(id);
                setProductDetail(response.data);
            } catch (error) {
                toast.error('Product does not exist', error);
            } finally {
                setIsLoading(false); // Kết thúc loading, không phụ thuộc vào thành công hay thất bại
            }
        };

        fetchProductDetail();
    }, [id]);

    const handleBuyNow = () => {
        toast.success('Added product to cart successfully');
        setTimeout(() => {
            navigate(`/pay?step=1`);
        }, 500);
    };

    useEffect(() => {
        if (selectedColor) {
            const selectedColorInfo = productDetail.colors.find((color) => color.name === selectedColor);
            setMaxQuantity(selectedColorInfo ? selectedColorInfo.quantity : 1);
        }
    }, [selectedColor, productDetail.colors]);

    const handleDecreaseQuantity = () => {
        if (quantityDefault > 1) {
            setQuantityDefault(quantityDefault - 1);
        }
    };

    const handleIncreaseQuantity = () => {
        if (quantityDefault < maxQuantity) {
            setQuantityDefault(quantityDefault + 1);
        } else {
            toast.warning(`You cannot add more than ${maxQuantity} entries for the selected color.`);
        }
    };

    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

    return (
        <>
            <Header cartItems={cartItems} />
            <div>
                <ToastContainer />
                {isLoading ? (
                    <div className="loading-spinner-container">
                        <div className="loading-spinner"></div>
                    </div>
                ) : (
                    <div className="about container-layout">
                        <div className="about-div">
                            <img src={productDetail.imageUrl} alt="" className="about-image"></img>
                            <div className="about-voucher">
                                <span className="about-voucher-text">{productDetail.discountPersent}%</span>
                            </div>
                        </div>
                        <div className="about-content">
                            <div className="about-information">
                                <h1 className="about-title">{productDetail.title}</h1>
                                <div className="about-rating">
                                    <Link to={`/product?brand=${productDetail?.brand?.name}`} className="about-brand">
                                        {productDetail?.brand?.name}
                                    </Link>
                                </div>
                                <div className="about-description">
                                    <p>{productDetail.description}</p>
                                </div>
                            </div>
                            <div className="about-table">
                                <div className="about-table-price">
                                    <span className="about-table-price-old">${productDetail.price}</span>
                                    <span className="about-table-price-current">${productDetail.discountedPrice}</span>
                                </div>
                                <div className="about-table-color">
                                    <span className="about-color-name">Color:</span>
                                    <div className="about-color-buttons">
                                        {productDetail.colors.map((color) => (
                                            <button
                                                key={color.name}
                                                className={`color-button ${
                                                    selectedColor === color.name ? 'selected' : ''
                                                }`}
                                                onClick={() => handleColorClick(color)}
                                            >
                                                {color.name}
                                            </button>
                                        ))}
                                    </div>
                                    {selectedColorQuantity !== null && (
                                        <span className="selected-color-quantity">
                                            ({selectedColorQuantity} available)
                                        </span>
                                    )}
                                </div>
                                <div className="about-quantity">
                                    <div className="about-quantity-detail">
                                        <Button text="-" onClick={handleDecreaseQuantity}>
                                            -
                                        </Button>
                                        <input
                                            type="text"
                                            className="about-quantity-input"
                                            value={quantityDefault}
                                            onChange={(e) => setQuantityDefault(e.target.value)}
                                        />
                                        <Button text="+" onClick={handleIncreaseQuantity}>
                                            +
                                        </Button>
                                    </div>
                                    <div className="about-payment">
                                        <Button text="Add To Cart" onClick={() => handleAddToCart(productDetail.id)}>
                                            Add To Cart
                                        </Button>
                                        <button className="about-pay" onClick={handleBuyNow}>
                                            Buy Now
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
            <CommentCard productId={id} />
        </>
    );
}
