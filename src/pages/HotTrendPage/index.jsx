import Header from '../../layouts/UserDefaultLayout/Header';
import ProductBoxList from '../../components/ProductBoxList';
import { useCart } from '~/api/user/CartContext';

import Footer from '~/layouts/UserDefaultLayout/Footer';

export default function HotTrendPage() {
    const { cartItems } = useCart();
    return (
        <>
            <Header cartItems={cartItems} />
            <ProductBoxList />
            <Footer />
        </>
    );
}
