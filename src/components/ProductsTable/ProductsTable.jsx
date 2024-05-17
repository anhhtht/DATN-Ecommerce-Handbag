import React, { useEffect, useState } from 'react';
import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { deleteProduct, findProducts } from '~/states/Product/Action';
import Button from '../Button';
import { ToastContainer } from 'react-toastify';

const ProductsTable = ({ handleProductUpdate }) => {
    const dispatch = useDispatch();
    const { products } = useSelector((store) => store);
    const jwt = sessionStorage.getItem('jwt');

    const pageSize = 5;
    const [currentPage, setCurrentPage] = useState(0);

    const handleProductDelete = (productId) => {
        dispatch(deleteProduct(productId));
    };

    const handleProductUpdateClick = (product) => {
        handleProductUpdate(product);
    };

    useEffect(() => {
        const data = {
            color: [], // Thay size bằng color ở đây
            minPrice: 0,
            maxPrice: 10000000,
            minDiscount: 0,
            brand: [],
            stock: null,
            sort: 'price_low',
            pageNumber: 0,
            pageSize: 100,
        };

        dispatch(findProducts(data));
    }, [jwt, dispatch, currentPage, products?.deletedProduct]);

    const totalPages = Math.ceil((products?.products?.totalElements || 0) / pageSize);

    const handlePageChange = (newPage) => {
        setCurrentPage(newPage);
    };

    return (
        <div>
            <ToastContainer />
            <TableContainer component={Paper} variant="outlined">
                <Table aria-label="product table">
                    <TableHead>
                        <TableRow>
                            <TableCell>Image</TableCell>
                            <TableCell>Brand</TableCell>
                            <TableCell>Title</TableCell>
                            <TableCell>Description</TableCell>
                            <TableCell>Price</TableCell>
                            <TableCell>Quantity</TableCell>
                            <TableCell>Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {products?.products?.content
                            ?.slice(currentPage * pageSize, (currentPage + 1) * pageSize)
                            .map((item) => (
                                <TableRow key={item?.id}>
                                    <TableCell>
                                        <img src={item?.imageUrl} alt="" />
                                    </TableCell>
                                    <TableCell>
                                        <img src={item?.brand?.imageUrl} alt="" />
                                    </TableCell>
                                    <TableCell>
                                        {item?.title?.length > 20 ? `${item?.title?.substring(0, 20)}...` : item?.title}
                                    </TableCell>
                                    <TableCell>
                                        {item?.description?.length > 50
                                            ? `${item?.description?.substring(0, 50)}...`
                                            : item?.description}
                                    </TableCell>
                                    <TableCell>
                                        {item?.price.toLocaleString('it-IT', {
                                            style: 'currency',
                                            currency: 'VND',
                                        })}
                                    </TableCell>
                                    <TableCell>{item?.quantity}</TableCell>
                                    <TableCell>
                                        <Button
                                            small
                                            primary
                                            rounded
                                            className="ml-0"
                                            onClick={() => handleProductUpdateClick(item)}
                                        >
                                            Update
                                        </Button>
                                        <Button
                                            small
                                            primary
                                            rounded
                                            className="ml-0"
                                            onClick={() => handleProductDelete(item?.id)}
                                        >
                                            Delete
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <div className="pagination align-content-center d-flex justify-content-between pagination w-100">
                <Button small text onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 0}>
                    Previous
                </Button>
                <span>{`Page ${currentPage + 1} of ${totalPages}`}</span>
                <Button
                    small
                    text
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages - 1}
                >
                    Next
                </Button>
            </div>
        </div>
    );
};

export default ProductsTable;
