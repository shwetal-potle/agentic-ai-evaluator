import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAllSubmissions } from '../services/api';
import { ChevronRight, Award, Clock, FileCode2 } from 'lucide-react';
import { motion } from 'framer-motion';

const Dashboard = () => {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadSubmissions();
  }, []);

  const loadSubmissions = async () => {
    try {
      const data = await getAllSubmissions();
      setSubmissions(data);
    } catch (error) {
      console.error("Failed to load submissions", error);
    } finally {
      setLoading(false);
    }
  };

  const getScoreColor = (score) => {
    if (!score) return 'text-textMuted bg-white/5';
    if (score >= 80) return 'text-emerald-400 bg-emerald-400/10 border-emerald-400/20';
    if (score >= 60) return 'text-amber-400 bg-amber-400/10 border-amber-400/20';
    return 'text-rose-400 bg-rose-400/10 border-rose-400/20';
  };

  return (
    <div className="space-y-8">
      <header className="mb-12">
        <h1 className="text-4xl font-bold mb-4 tracking-tight">Hackathon Submissions</h1>
        <p className="text-textMuted text-lg max-w-2xl">
          Review and evaluate incoming GenAI projects using our Multi-Agent AI System.
        </p>
      </header>
      
      {loading ? (
        <div className="flex justify-center py-20">
          <div className="w-8 h-8 rounded-full border-2 border-primary border-t-transparent animate-spin" />
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {submissions.map((sub, index) => {
            const hasResult = !!sub.evaluationResult;
            const score = hasResult ? sub.evaluationResult.overallScore : null;
            
            return (
              <motion.div
                key={sub.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3, delay: index * 0.1 }}
                className="glass-card flex flex-col group hover:border-primary/50 transition-colors"
              >
                <div className="p-6 flex-1 space-y-4">
                  <div className="flex justify-between items-start">
                    <div className="w-12 h-12 rounded-xl bg-white/5 flex items-center justify-center border border-white/10 group-hover:bg-primary/10 group-hover:text-primary transition-colors">
                      <FileCode2 className="w-6 h-6" />
                    </div>
                    {hasResult ? (
                      <div className={`px-3 py-1 rounded-full text-xs font-bold border flex items-center gap-1.5 ${getScoreColor(score)}`}>
                        <Award className="w-3.5 h-3.5" />
                        {score} / 100
                      </div>
                    ) : (
                      <div className="px-3 py-1 rounded-full text-xs font-medium border border-white/10 bg-white/5 flex items-center gap-1.5 text-textMuted">
                        <Clock className="w-3.5 h-3.5" />
                        Pending
                      </div>
                    )}
                  </div>
                  
                  <div>
                    <h3 className="text-xl font-bold truncate">{sub.projectTitle}</h3>
                    <p className="text-textMuted text-sm font-medium mt-1 truncate">Team: {sub.teamName}</p>
                  </div>
                  
                  <p className="text-textMuted text-sm line-clamp-2 leading-relaxed">
                    {sub.useCaseDescription}
                  </p>
                </div>
                
                <div className="p-4 border-t border-white/10 bg-black/20 mt-auto">
                  <Link
                    to={`/evaluation/${sub.id}`}
                    className="flex items-center justify-between text-sm font-medium hover:text-primary transition-colors pr-2"
                  >
                    <span>View Evaluation Details</span>
                    <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                  </Link>
                </div>
              </motion.div>
            );
          })}
          
          {submissions.length === 0 && (
            <div className="col-span-full py-20 text-center glass-card">
              <div className="w-16 h-16 rounded-full bg-white/5 border border-white/10 flex items-center justify-center mx-auto mb-4">
                <FileCode2 className="w-8 h-8 text-textMuted" />
              </div>
              <h3 className="text-lg font-bold mb-2">No algorithms judging yet</h3>
              <p className="text-textMuted mb-6 max-w-sm mx-auto">Upload a hackathon submission to get detailed insights and scores.</p>
              <Link to="/upload" className="btn-primary inline-flex">
                Create First Submission
              </Link>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
