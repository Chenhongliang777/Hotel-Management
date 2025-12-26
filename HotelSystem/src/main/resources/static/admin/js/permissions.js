// 权限管理工具
const permissions = {
    // 检查用户是否有某个角色
    hasRole(role) {
        const userRole = auth.getUserRole();
        return userRole === role;
    },

    // 检查用户是否有任一角色
    hasAnyRole(...roles) {
        const userRole = auth.getUserRole();
        return roles.includes(userRole);
    },

    // 检查用户是否是民宿经营者
    isOperator() {
        return this.hasRole('OPERATOR');
    },

    // 检查用户是否是民宿经营者（原经理或管理员）
    isOperatorOrAbove() {
        return this.hasRole('OPERATOR');
    },

    // 检查用户是否是前台或民宿经营者
    isReceptionistOrAbove() {
        return this.hasAnyRole('RECEPTIONIST', 'OPERATOR');
    },

    // 根据角色显示/隐藏元素
    showForRoles(elementId, ...roles) {
        const element = document.getElementById(elementId);
        if (element && this.hasAnyRole(...roles)) {
            element.style.display = '';
        } else if (element) {
            element.style.display = 'none';
        }
    },

    // 根据角色隐藏元素
    hideForRoles(elementId, ...roles) {
        const element = document.getElementById(elementId);
        if (element && this.hasAnyRole(...roles)) {
            element.style.display = 'none';
        }
    },

    // 检查权限，如果没有权限则显示提示并返回false
    checkPermission(allowedRoles, actionName) {
        if (!this.hasAnyRole(...allowedRoles)) {
            const roleName = this.getRoleDisplayName();
            alert(`权限不足：${roleName}无法${actionName}。请联系管理员。`);
            return false;
        }
        return true;
    },

    // 获取角色显示名称
    getRoleDisplayName() {
        const role = auth.getUserRole();
        const roleMap = {
            'OPERATOR': '民宿经营者',
            'RECEPTIONIST': '前台',
            'HOUSEKEEPING': '房务'
        };
        return roleMap[role] || role;
    }
};



