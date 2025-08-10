/* Minimal SPA with LocalStorage-backed data */
(function () {
  const root = document.getElementById('appRoot');
  const accountLink = document.getElementById('accountLink');
  const sellerLink = document.getElementById('sellerLink');
  const cartBadge = document.getElementById('cartBadge');
  const yearEl = document.getElementById('year');
  const searchInput = document.getElementById('searchInput');
  const searchBtn = document.getElementById('searchBtn');

  if (yearEl) yearEl.textContent = new Date().getFullYear();

  // Data bootstrap
  const store = {
    products: [],
    users: [],
    reviews: [],
    orders: [],
    cart: [],
    session: { userId: null },
    seq: { product: 1, user: 1, review: 1, order: 1 },
    version: 6
  };

  function load() {
    const data = JSON.parse(localStorage.getItem('ganimart') || 'null');
    if (!data) {
      seed();
      save();
      return store;
    }
    Object.assign(store, data);
    migrate();
    return store;
  }

  function save() {
    localStorage.setItem('ganimart', JSON.stringify(store));
  }

  function seed() {
    // Users: customer and seller
    store.users = [
      { id: 1, email: 'gani@example.com', name: 'Gani', role: 'customer', password: 'pass' },
      { id: 2, email: 'seller@example.com', name: 'Seller', role: 'seller', password: 'pass' }
    ];
    store.session.userId = null;
    store.seq.user = 3;

    // Products
    const demo = [
      { title: 'Wireless Headphones', price: 79.99, category: 'Electronics', seed: 'headphones' },
      { title: 'Smart Watch Series 5', price: 149.00, category: 'Wearables', seed: 'smartwatch' },
      { title: 'Ergonomic Office Chair', price: 229.00, category: 'Home & Office', seed: 'officechair' },
      { title: 'Gaming Mouse Pro', price: 39.00, category: 'Electronics', seed: 'gamingmouse' },
      { title: 'Yoga Mat Eco', price: 25.00, category: 'Sports', seed: 'yogamat' },
      { title: 'Stainless Water Bottle', price: 19.00, category: 'Outdoors', seed: 'waterbottle' },
      { title: 'Bluetooth Speaker', price: 59.00, category: 'Electronics', seed: 'speaker' },
      { title: 'Running Shoes', price: 89.00, category: 'Sports', seed: 'runningshoes' }
    ];
    store.products = demo.map((p, i) => ({
      id: i + 1,
      description: `${p.title} description`,
      rating: 4,
      image: (i === 0) ? 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&h=600&fit=crop' :
             (i === 1) ? 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&h=600&fit=crop' :
             (i === 2) ? 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&h=600&fit=crop' :
             (i === 3) ? 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&h=600&fit=crop' :
             (i === 4) ? 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800&h=600&fit=crop' :
             (i === 5) ? 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800&h=600&fit=crop' :
             (i === 6) ? 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=800&h=600&fit=crop' :
             'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=600&fit=crop',
      title: p.title,
      price: p.price,
      category: p.category,
      stock: [24,18,12,40,50,70,33,26][i]
    }));
    store.seq.product = demo.length + 1;
    store.reviews = [
      { id: 1, productId: 1, userId: 1, rating: 5, comment: 'Great sound and battery!' }
    ];
    store.seq.review = 2;
    store.orders = [];
    store.seq.order = 1;
    store.cart = [];
    store.version = 6;
  }

  function migrate() {
    // Initialize version if missing
    if (!store.version) store.version = 1;

    // v2: ensure all product images use a reliable source
    if (store.version < 2) {
      store.products = store.products.map(p => ({
        ...p,
        image: p.image && p.image.startsWith('http') ? p.image : `https://picsum.photos/seed/p${p.id}/800/600`
      }));
      store.version = 2;
      save();
    }

    // v3: move to local assets to avoid external loading issues
    if (store.version < 3) {
      store.products = store.products.map((p, idx) => ({
        ...p,
        image: `assets/p${((idx % 8) + 1)}.svg`
      }));
      store.version = 3;
      save();
    }

    // v4: force-correct any bad asset paths and unify to local assets
    if (store.version < 4) {
      store.products = store.products.map((p, idx) => ({
        ...p,
        image: `assets/p${((idx % 8) + 1)}.svg`
      }));
      store.version = 4;
      save();
    }

    // v5: introduce imageLocal and prefer external photo first
    if (store.version < 5) {
      store.products = store.products.map((p, idx) => {
        const local = `assets/p${((idx % 8) + 1)}.svg`;
        const seed = encodeURIComponent((p.title || `product${idx}`).replace(/\s+/g, '')) + idx;
        const external = `https://picsum.photos/seed/${seed}/800/600`;
        // If current p.image is an external URL, keep it; otherwise use external and store local as fallback
        const isExternal = typeof p.image === 'string' && /^https?:\/\//.test(p.image);
        return {
          ...p,
          imageLocal: p.imageLocal || local,
          image: isExternal ? p.image : external
        };
      });
      store.version = 5;
      save();
    }

    // v6: force specific items to use local images (to avoid flaky remotes)
    if (store.version < 6) {
      store.products = store.products.map(p => {
        const t = (p.title || '').toLowerCase();
        if (t.includes('wireless headphones')) {
          return { ...p, image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&h=600&fit=crop' };
        }
        if (t.includes('smart watch')) {
          return { ...p, image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&h=600&fit=crop' };
        }
        if (t.includes('ergonomic office chair')) {
          return { ...p, image: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&h=600&fit=crop' };
        }
        if (t.includes('gaming mouse')) {
          return { ...p, image: 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&h=600&fit=crop' };
        }
        if (t.includes('yoga mat')) {
          return { ...p, image: 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800&h=600&fit=crop' };
        }
        if (t.includes('water bottle')) {
          return { ...p, image: 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800&h=600&fit=crop' };
        }
        if (t.includes('bluetooth speaker')) {
          return { ...p, image: 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=800&h=600&fit=crop' };
        }
        if (t.includes('running shoes')) {
          return { ...p, image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=600&fit=crop' };
        }
        return p;
      });
      store.version = 6;
      save();
    }
  }

  load();

  // Helpers
  function getCurrentUser() { return store.users.find(u => u.id === store.session.userId) || null; }
  function isSeller() { const u = getCurrentUser(); return !!u && u.role === 'seller'; }
  function formatPrice(n) { return `$${n.toFixed(2)}`; }
  function updateHeader() {
    const user = getCurrentUser();
    accountLink.textContent = user ? user.name : 'Login';
    accountLink.href = user ? '#profile' : '#auth';
    sellerLink.classList.toggle('hide', !isSeller());
    const count = store.cart.reduce((s, i) => s + i.qty, 0);
    cartBadge.textContent = count;
  }

  function navigate(hash) {
    if (!hash) hash = '#home';
    const route = hash.split('?')[0];
    if (route === '#home') renderHome();
    else if (route === '#catalog') renderCatalog();
    else if (route.startsWith('#product/')) {
      const id = Number(route.split('/')[1]);
      renderProduct(id);
    } else if (route === '#cart') renderCart();
    else if (route === '#checkout') renderCheckout();
    else if (route === '#confirm') renderConfirmation();
    else if (route === '#orders') renderOrders();
    else if (route === '#auth') renderAuth();
    else if (route === '#profile') renderProfile();
    else if (route === '#seller') renderSeller();
    else renderHome();
    updateHeader();
  }

  // Rendering
  function renderHome() {
    root.innerHTML = `
      <section class="panel stack">
        <div class="card center" style="height: 160px; background: linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200&h=400&fit=crop') center/cover; position: relative;">
          <div style="position: relative; z-index: 2; padding: 20px; border-radius: 12px;">
            <h1 style="margin: 0 0 8px 0; color: #ffffff;">Discover products you love</h1>
            <p style="margin: 0 0 16px 0; color: #e6eaf2; font-size: 16px;">Fast search, clear details, simple checkout.</p>
            <div class="row" style="justify-content:center;gap:12px;">
              <a href="#catalog" class="btn btn-primary">Shop now</a>
              <a href="#seller" class="btn" style="background: rgba(255,255,255,0.9); color: #1a1f2e; border: none;">Sell on GaniMart</a>
            </div>
          </div>
        </div>
        <div class="stack">
          <h3>Featured</h3>
          <div class="grid" id="featuredGrid"></div>
        </div>
      </section>
    `;
    const grid = document.getElementById('featuredGrid');
    grid.innerHTML = store.products.slice(0, 8).map(renderProductCard).join('');
    bindProductCardActions(grid);
    bindImageFallbacks(grid);
  }

  function renderCatalog() {
    const categories = Array.from(new Set(store.products.map(p => p.category))).sort();
    root.innerHTML = `
      <section class="stack">
        <div class="row">
          <div class="panel" style="min-width:220px;max-width:260px;">
            <div class="form">
              <div class="field">
                <label>Category</label>
                <select id="filterCategory">
                  <option value="">All</option>
                  ${categories.map(c => `<option value="${c}">${c}</option>`).join('')}
                </select>
              </div>
              <div class="field">
                <label>Price up to</label>
                <input id="filterPrice" type="number" min="0" step="1" placeholder="No limit" />
              </div>
              <button id="applyFilters" class="btn">Apply</button>
            </div>
          </div>
          <div class="stack" style="flex:1">
            <div class="row" style="justify-content:space-between;align-items:center;">
              <h2>Catalog</h2>
              <span class="pill" id="resultsCount"></span>
            </div>
            <div class="grid" id="catalogGrid"></div>
          </div>
        </div>
      </section>
    `;

    const grid = document.getElementById('catalogGrid');
    const resultsCount = document.getElementById('resultsCount');
    const categoryEl = document.getElementById('filterCategory');
    const priceEl = document.getElementById('filterPrice');
    const applyBtn = document.getElementById('applyFilters');

    function refresh() {
      let list = [...store.products];
      const q = (searchInput && searchInput.value || '').trim().toLowerCase();
      if (q) list = list.filter(p => p.title.toLowerCase().includes(q));
      const cat = categoryEl.value;
      if (cat) list = list.filter(p => p.category === cat);
      const price = Number(priceEl.value);
      if (!Number.isNaN(price) && price > 0) list = list.filter(p => p.price <= price);
      resultsCount.textContent = `${list.length} results`;
      grid.innerHTML = list.map(renderProductCard).join('');
      bindProductCardActions(grid);
      bindImageFallbacks(grid);
    }

    applyBtn.addEventListener('click', refresh);
    refresh();
  }

  function renderProductCard(p) {
    return `
      <div class="card product-card" data-id="${p.id}">
        <img src="${p.image}" alt="${p.title}" referrerpolicy="no-referrer" />
        <h4>${p.title}</h4>
        <div class="row" style="justify-content:space-between;align-items:center;">
          <span class="price">${formatPrice(p.price)}</span>
          <span class="rating">${'★'.repeat(Math.round(p.rating || 4))}${'☆'.repeat(5 - Math.round(p.rating || 4))}</span>
        </div>
        <div class="row" style="margin-top:8px;">
          <a class="btn" href="#product/${p.id}">View</a>
          <button class="btn btn-primary addToCart">Add</button>
        </div>
      </div>
    `;
  }

  function bindProductCardActions(container) {
    container.querySelectorAll('.addToCart').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = Number(e.target.closest('.product-card').dataset.id);
        addToCart(id, 1);
        updateHeader();
      });
    });
  }

  function renderProduct(id) {
    const p = store.products.find(x => x.id === id);
    if (!p) { root.innerHTML = '<div class="panel">Product not found.</div>'; return; }
    const productReviews = store.reviews.filter(r => r.productId === id);
    const avg = productReviews.length ? (productReviews.reduce((s, r) => s + r.rating, 0) / productReviews.length) : (p.rating || 0);
    root.innerHTML = `
      <section class="split">
        <div class="panel">
          <img id="detailImg" src="${p.image}" alt="${p.title}" referrerpolicy="no-referrer" style="width:100%;border-radius:12px;max-height:420px;object-fit:cover;" />
        </div>
        <div class="stack">
          <div class="panel">
            <h2>${p.title}</h2>
            <div class="row" style="gap:8px;align-items:center;">
              <span class="price">${formatPrice(p.price)}</span>
              <span class="rating">${'★'.repeat(Math.round(avg))}${'☆'.repeat(5 - Math.round(avg))}</span>
              <span class="pill">In stock: ${p.stock}</span>
            </div>
            <div class="row" style="margin-top:8px;">
              <input id="qty" type="number" min="1" value="1" style="width:90px" />
              <button id="addBtn" class="btn btn-primary">Add to Cart</button>
            </div>
            <p class="muted" style="margin-top:12px;">${p.description}</p>
          </div>
          <div class="panel">
            <div class="row" style="justify-content:space-between;align-items:center;">
              <h3>Reviews</h3>
              <button id="writeReview" class="btn">Write Review</button>
            </div>
            <div id="reviewsList" class="stack" style="margin-top:8px;"></div>
          </div>
        </div>
      </section>
    `;
    // detail image fallback
    const dimg = document.getElementById('detailImg');
    if (dimg) attachImgFallback(dimg, p.title);
    document.getElementById('addBtn').addEventListener('click', () => {
      const qty = Math.max(1, Number(document.getElementById('qty').value) || 1);
      addToCart(id, qty);
      updateHeader();
    });

    document.getElementById('writeReview').addEventListener('click', () => {
      const user = getCurrentUser();
      if (!user) { location.hash = '#auth'; return; }
      const purchased = store.orders.some(o => o.userId === user.id && o.items.some(it => it.productId === id));
      if (!purchased) { alert('Only customers who purchased this product can write a review.'); return; }
      const rating = Number(prompt('Rating 1-5', '5'));
      const comment = prompt('Comment', 'Loved it!') || '';
      if (!rating || rating < 1 || rating > 5) return;
      store.reviews.push({ id: store.seq.review++, productId: id, userId: user.id, rating, comment });
      save();
      renderProduct(id);
    });

    const list = document.getElementById('reviewsList');
    if (!productReviews.length) list.innerHTML = '<div class="muted">No reviews yet.</div>';
    else list.innerHTML = productReviews.map(r => {
      const u = store.users.find(u => u.id === r.userId);
      return `<div class="card"><div class="row" style="justify-content:space-between;">
        <strong>${u ? u.name : 'User'}</strong>
        <span class="rating">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</span>
      </div><p>${r.comment}</p></div>`;
    }).join('');
  }

  function renderCart() {
    const items = store.cart.map(ci => ({ ...ci, product: store.products.find(p => p.id === ci.productId) })).filter(ci => ci.product);
    const total = items.reduce((s, i) => s + i.product.price * i.qty, 0);
    root.innerHTML = `
      <section class="stack">
        <h2>Shopping Cart</h2>
        <div class="panel">
          ${items.length === 0 ? '<div class="muted">Your cart is empty.</div>' : `
          <table>
            <thead><tr><th>Product</th><th>Qty</th><th>Price</th><th>Subtotal</th><th></th></tr></thead>
            <tbody>
            ${items.map(i => `
              <tr data-id="${i.productId}">
                <td>${i.product.title}</td>
                <td><input class="qty" type="number" min="1" value="${i.qty}" style="width:80px" /></td>
                <td>${formatPrice(i.product.price)}</td>
                <td>${formatPrice(i.product.price * i.qty)}</td>
                <td><button class="btn btn-danger remove">Remove</button></td>
              </tr>
            `).join('')}
            </tbody>
          </table>
          <div class="row" style="justify-content:space-between;align-items:center;margin-top:12px;">
            <a class="btn" href="#catalog">Continue shopping</a>
            <div class="row" style="gap:12px;align-items:center;">
              <strong>Total: ${formatPrice(total)}</strong>
              <a class="btn btn-primary" href="#checkout">Checkout</a>
            </div>
          </div>`}
        </div>
      </section>
    `;
    root.querySelectorAll('input.qty').forEach(inp => {
      inp.addEventListener('change', (e) => {
        const tr = e.target.closest('tr');
        const id = Number(tr.dataset.id);
        const qty = Math.max(1, Number(e.target.value) || 1);
        const item = store.cart.find(c => c.productId === id);
        if (item) item.qty = qty;
        save();
        renderCart();
        updateHeader();
      });
    });
    root.querySelectorAll('button.remove').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const tr = e.target.closest('tr');
        const id = Number(tr.dataset.id);
        store.cart = store.cart.filter(c => c.productId !== id);
        save();
        renderCart();
        updateHeader();
      });
    });
  }

  function renderCheckout() {
    if (store.cart.length === 0) { location.hash = '#cart'; return; }
    const user = getCurrentUser();
    const name = user ? user.name : '';
    root.innerHTML = `
      <section class="stack">
        <h2>Checkout</h2>
        <div class="split">
          <div class="panel">
            <h3>Shipping</h3>
            <div class="form">
              <div class="row">
                <div class="field" style="flex:1">
                  <label>Full name</label>
                  <input id="shipName" value="${name}" placeholder="Jane Doe" />
                </div>
                <div class="field" style="width:160px">
                  <label>Phone</label>
                  <input id="shipPhone" value="" placeholder="+1 555 000 000" />
                </div>
              </div>
              <div class="field"><label>Address</label><input id="shipAddr" placeholder="1 Main St" /></div>
              <div class="row">
                <div class="field" style="flex:1"><label>City</label><input id="shipCity" placeholder="City" /></div>
                <div class="field" style="width:120px"><label>ZIP</label><input id="shipZip" placeholder="00000" /></div>
              </div>
            </div>
          </div>
          <div class="panel">
            <h3>Payment</h3>
            <div class="form">
              <div class="field"><label>Card number</label><input id="cardNum" placeholder="4242 4242 4242 4242" /></div>
              <div class="row">
                <div class="field"><label>Exp</label><input id="cardExp" placeholder="12/29" /></div>
                <div class="field"><label>CVC</label><input id="cardCvc" placeholder="123" /></div>
              </div>
              <button id="placeOrder" class="btn btn-primary">Place Order</button>
            </div>
          </div>
        </div>
      </section>
    `;
    document.getElementById('placeOrder').addEventListener('click', () => {
      const orderId = store.seq.order++;
      const items = store.cart.map(ci => ({ productId: ci.productId, qty: ci.qty }));
      const total = items.reduce((s, i) => {
        const p = store.products.find(pp => pp.id === i.productId); return s + (p ? p.price * i.qty : 0);
      }, 0);
      const order = {
        id: orderId,
        number: `#${String(orderId).padStart(6, '0')}`,
        userId: store.session.userId,
        items,
        total,
        createdAt: new Date().toISOString(),
        status: 'Processing'
      };
      store.orders.unshift(order);
      store.cart = [];
      save();
      location.hash = '#confirm';
      updateHeader();
    });
  }

  function renderConfirmation() {
    const last = store.orders[0];
    root.innerHTML = `
      <section class="panel center" style="min-height:260px;">
        <div class="stack" style="place-items:center;">
          <h2>Thank you!</h2>
          <p class="muted">Your order ${last ? last.number : ''} has been placed.</p>
          <div class="row">
            <a class="btn" href="#orders">View Orders</a>
            <a class="btn btn-primary" href="#catalog">Continue Shopping</a>
          </div>
        </div>
      </section>
    `;
  }

  function renderOrders() {
    const user = getCurrentUser();
    const myOrders = user ? store.orders.filter(o => o.userId === user.id) : [];
    root.innerHTML = `
      <section class="stack">
        <h2>Orders</h2>
        <div class="panel">
          ${myOrders.length === 0 ? '<div class="muted">No orders yet.</div>' : `
          <table>
            <thead><tr><th>Order</th><th>Date</th><th>Status</th><th>Total</th></tr></thead>
            <tbody>
              ${myOrders.map(o => `<tr><td>${o.number}</td><td>${new Date(o.createdAt).toLocaleString()}</td><td>${o.status}</td><td>${formatPrice(o.total)}</td></tr>`).join('')}
            </tbody>
          </table>`}
        </div>
      </section>
    `;
  }

  function renderAuth() {
    root.innerHTML = `
      <section class="split">
        <div class="panel">
          <h3>Login</h3>
          <div class="form">
            <div class="field"><label>Email</label><input id="loginEmail" placeholder="gani@example.com" value="gani@example.com" /></div>
            <div class="field"><label>Password</label><input id="loginPass" type="password" placeholder="pass" value="pass" /></div>
            <button id="loginBtn" class="btn btn-primary">Login</button>
          </div>
        </div>
        <div class="panel">
          <h3>Register</h3>
          <div class="form">
            <div class="field"><label>Name</label><input id="regName" placeholder="Your name" /></div>
            <div class="field"><label>Email</label><input id="regEmail" placeholder="you@example.com" /></div>
            <div class="field"><label>Password</label><input id="regPass" type="password" placeholder="Choose a password" /></div>
            <button id="regBtn" class="btn">Create account</button>
          </div>
        </div>
      </section>
    `;
    document.getElementById('loginBtn').addEventListener('click', () => {
      const email = document.getElementById('loginEmail').value.trim();
      const pass = document.getElementById('loginPass').value;
      const u = store.users.find(u => u.email === email && u.password === pass);
      if (!u) { alert('Invalid credentials'); return; }
      store.session.userId = u.id; save();
      location.hash = '#profile';
      updateHeader();
    });
    document.getElementById('regBtn').addEventListener('click', () => {
      const name = document.getElementById('regName').value.trim();
      const email = document.getElementById('regEmail').value.trim();
      const pass = document.getElementById('regPass').value;
      if (!name || !email || !pass) { alert('Fill all fields'); return; }
      if (store.users.some(u => u.email === email)) { alert('Email already used'); return; }
      const id = store.seq.user++;
      store.users.push({ id, email, name, role: 'customer', password: pass });
      store.session.userId = id; save();
      location.hash = '#profile';
      updateHeader();
    });
  }

  function renderProfile() {
    const u = getCurrentUser();
    if (!u) { location.hash = '#auth'; return; }
    root.innerHTML = `
      <section class="stack">
        <div class="panel row" style="justify-content:space-between;align-items:center;">
          <div>
            <h3>Welcome, ${u.name}</h3>
            <div class="muted">${u.email} — Role: ${u.role}</div>
          </div>
          <div class="row">
            <a class="btn" href="#orders">Orders</a>
            <button id="logoutBtn" class="btn btn-ghost">Logout</button>
          </div>
        </div>
        <div class="panel">
          <h4>Addresses</h4>
          <div class="muted">(Demo) Add at checkout</div>
        </div>
      </section>
    `;
    document.getElementById('logoutBtn').addEventListener('click', () => {
      store.session.userId = null; save();
      location.hash = '#home';
      updateHeader();
    });
  }

  function renderSeller() {
    const u = getCurrentUser();
    if (!u) { location.hash = '#auth'; return; }
    if (u.role !== 'seller') {
      // Allow toggling role for demo purposes
      if (confirm('Switch your role to seller for demo?')) { u.role = 'seller'; save(); }
    }
    const myProducts = store.products.filter(p => (p.ownerId || 2) === u.id);
    root.innerHTML = `
      <section class="stack">
        <div class="panel">
          <h3>Seller Dashboard</h3>
          <div class="form">
            <div class="row">
              <div class="field" style="flex:1"><label>Title</label><input id="pTitle" /></div>
              <div class="field" style="width:160px"><label>Price</label><input id="pPrice" type="number" min="0" step="0.01" /></div>
              <div class="field" style="width:160px"><label>Stock</label><input id="pStock" type="number" min="0" step="1" value="10"/></div>
            </div>
            <div class="row">
              <div class="field" style="width:220px"><label>Category</label><input id="pCat" placeholder="Electronics"/></div>
              <div class="field" style="flex:1"><label>Image URL</label><input id="pImg" placeholder="https://..."/></div>
            </div>
            <div class="field"><label>Description</label><textarea id="pDesc" rows="3"></textarea></div>
            <button id="addProduct" class="btn btn-primary">Add Product</button>
          </div>
        </div>
        <div class="panel">
          <h4>Your Products</h4>
          <div class="grid" id="sellerGrid"></div>
        </div>
      </section>
    `;
    const grid = document.getElementById('sellerGrid');
    grid.innerHTML = myProducts.map(renderProductCard).join('');
    bindProductCardActions(grid);
    bindImageFallbacks(grid);

    document.getElementById('addProduct').addEventListener('click', () => {
      const title = document.getElementById('pTitle').value.trim();
      const price = Number(document.getElementById('pPrice').value);
      const stock = Number(document.getElementById('pStock').value);
      const category = document.getElementById('pCat').value.trim() || 'Misc';
      const provided = document.getElementById('pImg').value.trim();
      const image = provided || `https://picsum.photos/seed/product${store.seq.product}/800/600`;
      const description = document.getElementById('pDesc').value.trim();
      if (!title || !price || price < 0) { alert('Provide title and valid price'); return; }
      const id = store.seq.product++;
      store.products.unshift({ id, title, price, stock: Math.max(0, stock||0), category, image, description, rating: 4, ownerId: u.id });
      save();
      renderSeller();
    });
  }

  function addToCart(productId, qty) {
    const item = store.cart.find(c => c.productId === productId);
    if (item) item.qty += qty; else store.cart.push({ productId, qty });
    save();
  }

  // Image fallback utilities
  function makePlaceholder(text) {
    const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='600' height='400'>
      <rect width='100%' height='100%' fill='#1b2030'/>
      <text x='50%' y='50%' dominant-baseline='middle' text-anchor='middle' fill='#a7b0c0' font-family='Inter, Arial' font-size='20'>${(text || 'Image not available').slice(0,40)}</text>
    </svg>`;
    return 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(svg);
  }
  function attachImgFallback(img, title) {
    try { img.referrerPolicy = 'no-referrer'; } catch (_) {}
    img.addEventListener('error', () => {
      img.src = makePlaceholder(title || img.alt || '');
    });
  }
  function bindImageFallbacks(container) {
    container.querySelectorAll('img').forEach(img => attachImgFallback(img, img.getAttribute('alt') || ''));
  }

  // Search
  if (searchBtn) searchBtn.addEventListener('click', () => { location.hash = '#catalog'; });
  if (searchInput) searchInput.addEventListener('keydown', (e) => { if (e.key === 'Enter') location.hash = '#catalog'; });

  // Router
  window.addEventListener('hashchange', () => navigate(location.hash));
  navigate(location.hash || '#home');
})();


// Reset demo handler
document.addEventListener('DOMContentLoaded', () => {
  const reset = document.getElementById('resetDemo');
  if (reset) {
    reset.addEventListener('click', (e) => {
      e.preventDefault();
      if (confirm('Reset demo data?')) {
        localStorage.removeItem('ganimart');
        location.reload();
      }
    });
  }
});

