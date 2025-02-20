/*
to consider:
- should we show CTNs on Product Cards?
- should we show 'view product'?
*/

$('.toggle-grid').on('click', function() {
    $('.ph-product-card__wrapper').toggleClass('ph-product-card__wrapper--list');
    $('.ph-product-card__wrapper').toggleClass('ph-product-card__fake-facets');
});

$('.toggle-size').on('click', function() {
    $('.ph-product-card').toggleClass('ph-product-card--wide');
    $('.ph-product-card__wrapper').toggleClass('ph-product-card__fake-facets');
});

$('.toggle-compare').on('click', function() {
    $('.ph-product-card__compare').toggle();
});

$('.toggle-ribbon').on('click', function() {
    $('.ph-product-card__ribbon').toggle();
});

$('.toggle-subscribe').on('click', function() {
    $('.ph-product-card__subscription').toggle();
});

$('.toggle-features').on('click', function() {
    $('.ph-product-card__features').toggle();
});

$('.toggle-previous').on('click', function() {
    $('.ph-product-card__previous-price').toggle();
});
