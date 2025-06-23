/**
 * jshERP 生产管理模块路由测试脚本
 * 在浏览器控制台中运行此脚本来验证路由配置
 */

// 测试函数：检查路由是否正确生成
function testProductionRoutes() {
  console.log('🔍 开始测试生产管理模块路由配置...\n');
  
  // 1. 检查当前路由器实例
  if (!window.Vue || !window.Vue.prototype.$router) {
    console.error('❌ Vue Router 实例未找到');
    return;
  }
  
  const router = window.Vue.prototype.$router;
  const routes = router.options.routes;
  const addRoutes = window.Vue.prototype.$store?.getters?.addRouters || [];
  
  console.log('📋 当前路由配置：');
  console.log('基础路由数量：', routes.length);
  console.log('动态路由数量：', addRoutes.length);
  
  // 2. 查找生产管理相关路由
  const productionRoutes = [];
  
  // 在基础路由中查找
  routes.forEach(route => {
    if (route.path && route.path.includes('production')) {
      productionRoutes.push({ source: 'base', route });
    }
    if (route.children) {
      route.children.forEach(child => {
        if (child.path && child.path.includes('production')) {
          productionRoutes.push({ source: 'base-child', route: child });
        }
      });
    }
  });
  
  // 在动态路由中查找
  addRoutes.forEach(route => {
    if (route.path && route.path.includes('production')) {
      productionRoutes.push({ source: 'dynamic', route });
    }
    if (route.children) {
      route.children.forEach(child => {
        if (child.path && child.path.includes('production')) {
          productionRoutes.push({ source: 'dynamic-child', route: child });
        }
      });
    }
  });
  
  console.log('\n🎯 找到的生产管理路由：');
  if (productionRoutes.length === 0) {
    console.warn('⚠️  未找到生产管理相关路由');
  } else {
    productionRoutes.forEach((item, index) => {
      console.log(`${index + 1}. [${item.source}] ${item.route.path} - ${item.route.name || item.route.meta?.title || '未命名'}`);
    });
  }
  
  // 3. 测试路由跳转
  console.log('\n🚀 测试路由跳转：');
  const testPaths = [
    '/production/order',
    '/production/kanban', 
    '/production/post-task'
  ];
  
  testPaths.forEach(path => {
    try {
      const resolved = router.resolve(path);
      if (resolved.resolved.matched.length > 0) {
        console.log(`✅ ${path} - 路由解析成功`);
      } else {
        console.log(`❌ ${path} - 路由解析失败`);
      }
    } catch (error) {
      console.log(`❌ ${path} - 路由解析异常:`, error.message);
    }
  });
  
  // 4. 检查权限列表
  console.log('\n🔐 检查权限配置：');
  const permissionList = window.Vue.prototype.$store?.getters?.permissionList || [];
  console.log('权限列表长度：', permissionList.length);
  
  const productionPermissions = permissionList.filter(item => 
    item.text && item.text.includes('生产') || 
    item.url && item.url.includes('production')
  );
  
  if (productionPermissions.length > 0) {
    console.log('✅ 找到生产管理权限：');
    productionPermissions.forEach(perm => {
      console.log(`  - ${perm.text} (${perm.url})`);
    });
  } else {
    console.warn('⚠️  未找到生产管理权限');
  }
  
  // 5. 检查菜单显示
  console.log('\n📱 检查菜单显示：');
  const menuItems = document.querySelectorAll('.ant-menu-item, .ant-menu-submenu');
  let foundProductionMenu = false;
  
  menuItems.forEach(item => {
    const text = item.textContent || item.innerText;
    if (text && text.includes('生产管理')) {
      foundProductionMenu = true;
      console.log('✅ 在菜单中找到"生产管理"');
    }
  });
  
  if (!foundProductionMenu) {
    console.warn('⚠️  在菜单中未找到"生产管理"');
  }
  
  console.log('\n📊 测试总结：');
  console.log(`- 生产管理路由数量: ${productionRoutes.length}`);
  console.log(`- 生产管理权限数量: ${productionPermissions.length}`);
  console.log(`- 菜单显示状态: ${foundProductionMenu ? '正常' : '异常'}`);
  
  return {
    routes: productionRoutes,
    permissions: productionPermissions,
    menuVisible: foundProductionMenu
  };
}

// 测试函数：尝试跳转到生产管理页面
function navigateToProduction(path = '/production/kanban') {
  console.log(`🔄 尝试跳转到: ${path}`);
  
  if (!window.Vue || !window.Vue.prototype.$router) {
    console.error('❌ Vue Router 实例未找到');
    return;
  }
  
  try {
    window.Vue.prototype.$router.push(path);
    console.log('✅ 跳转命令已发送');
  } catch (error) {
    console.error('❌ 跳转失败:', error.message);
  }
}

// 测试函数：检查组件是否正确加载
function checkComponentLoading() {
  console.log('🔍 检查组件加载状态...\n');
  
  const currentRoute = window.Vue.prototype.$route;
  if (!currentRoute) {
    console.error('❌ 当前路由信息未找到');
    return;
  }
  
  console.log('当前路由信息：');
  console.log('- 路径:', currentRoute.path);
  console.log('- 名称:', currentRoute.name);
  console.log('- 组件:', currentRoute.matched.map(m => m.components?.default?.name || '未知'));
  
  // 检查是否有错误
  const errorElements = document.querySelectorAll('.ant-result-error, .error-page, [class*="error"]');
  if (errorElements.length > 0) {
    console.warn('⚠️  页面可能存在错误元素');
  } else {
    console.log('✅ 页面加载正常');
  }
}

// 导出测试函数到全局
window.testProductionRoutes = testProductionRoutes;
window.navigateToProduction = navigateToProduction;
window.checkComponentLoading = checkComponentLoading;

// 使用说明
console.log(`
🧪 jshERP 生产管理路由测试工具已加载

使用方法：
1. testProductionRoutes() - 全面测试路由配置
2. navigateToProduction('/production/kanban') - 跳转到指定页面
3. checkComponentLoading() - 检查当前页面组件加载状态

示例：
> testProductionRoutes()
> navigateToProduction('/production/order')
> checkComponentLoading()
`);

// 如果在生产环境，自动运行基础测试
if (typeof window !== 'undefined' && window.location.hostname !== 'localhost') {
  setTimeout(() => {
    console.log('🚀 自动运行路由测试...');
    testProductionRoutes();
  }, 2000);
}
