import React from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, PlusCircle, Sparkles } from 'lucide-react';
import { motion } from 'framer-motion';

// Pages - to be implemented
import Dashboard from './pages/Dashboard';
import SubmissionUpload from './pages/SubmissionUpload';
import EvaluationReport from './pages/EvaluationReport';

const Navbar = () => {
  const location = useLocation();

  const navItems = [
    { name: 'Dashboard', path: '/', icon: LayoutDashboard },
    { name: 'New Submission', path: '/upload', icon: PlusCircle },
  ];

  return (
    <nav className="border-b border-white/10 bg-surface/50 backdrop-blur-xl sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-3 group">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-primary to-accent flex items-center justify-center shadow-lg shadow-primary/20 group-hover:scale-105 transition-transform">
            <Sparkles className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-bold bg-gradient-to-r from-white to-textMuted bg-clip-text text-transparent">
              AgentEvaluator
            </h1>
            <p className="text-xs text-textMuted uppercase tracking-wider font-medium">GenAI Hackathons</p>
          </div>
        </Link>
        <div className="flex gap-2">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            const Icon = item.icon;
            
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  isActive 
                  ? 'bg-white/10 text-white shadow-inner' 
                  : 'text-textMuted hover:text-white hover:bg-white/5'
                }`}
              >
                <Icon className="w-4 h-4" />
                {item.name}
              </Link>
            );
          })}
        </div>
      </div>
    </nav>
  );
};

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen relative overflow-hidden flex flex-col">
        {/* Background glow effects */}
        <div className="absolute top-[-20%] left-[-10%] w-[50%] h-[50%] rounded-full bg-primary/20 blur-[120px] pointer-events-none" />
        <div className="absolute bottom-[-20%] right-[-10%] w-[50%] h-[50%] rounded-full bg-accent/20 blur-[120px] pointer-events-none" />
        
        <Navbar />
        
        <main className="flex-1 max-w-7xl w-full mx-auto px-6 py-12 relative z-10">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/upload" element={<SubmissionUpload />} />
              <Route path="/evaluation/:id" element={<EvaluationReport />} />
            </Routes>
          </motion.div>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
