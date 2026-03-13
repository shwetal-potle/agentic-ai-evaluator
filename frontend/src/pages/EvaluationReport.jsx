import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getSubmissionDetails, triggerEvaluation } from '../services/api';
import { Bot, Sparkles, Activity, FileCode, Beaker, ShieldCheck, Star } from 'lucide-react';
import { motion } from 'framer-motion';
import { Radar, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip } from 'recharts';

const AGENT_COLORS = {
  "Software Engineering Agent": "text-blue-400 bg-blue-400/10 border-blue-400/20",
  "GenAI Agent": "text-purple-400 bg-purple-400/10 border-purple-400/20",
  "Prompt Quality Agent": "text-amber-400 bg-amber-400/10 border-amber-400/20",
  "Code Analysis Agent": "text-emerald-400 bg-emerald-400/10 border-emerald-400/20"
};

const AGENT_ICONS = {
  "Software Engineering Agent": <FileCode className="w-5 h-5" />,
  "GenAI Agent": <Sparkles className="w-5 h-5" />,
  "Prompt Quality Agent": <Beaker className="w-5 h-5" />,
  "Code Analysis Agent": <ShieldCheck className="w-5 h-5" />
};

const EvaluationReport = () => {
  const { id } = useParams();
  const [submission, setSubmission] = useState(null);
  const [loading, setLoading] = useState(true);
  const [evaluating, setEvaluating] = useState(false);

  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    try {
      const data = await getSubmissionDetails(id);
      setSubmission(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleEvaluate = async () => {
    setEvaluating(true);
    try {
      await triggerEvaluation(id);
      await loadData();
    } catch (e) {
      console.error(e);
      alert("Evaluation failed. Have you configured the Gemini API Key?");
    } finally {
      setEvaluating(false);
    }
  };

  if (loading) return (
    <div className="flex h-[50vh] items-center justify-center">
      <div className="w-10 h-10 rounded-full border-4 border-primary border-t-transparent animate-spin" />
    </div>
  );

  if (!submission) return <div className="text-center text-xl mt-20">Submission not found</div>;

  const result = submission.evaluationResult;

  if (!result) {
    return (
      <div className="max-w-3xl mx-auto text-center py-20 glass-card">
        <Bot className="w-20 h-20 mx-auto text-primary mb-6 animate-pulse" />
        <h2 className="text-3xl font-bold mb-4">Ready for AI Evaluation</h2>
        <p className="text-textMuted mb-8 text-lg max-w-lg mx-auto">
          The submission "{submission.projectTitle}" by {submission.teamName} is ready to be analyzed by the 5-agent jury.
        </p>
        <button 
          onClick={handleEvaluate} 
          disabled={evaluating}
          className={`btn-primary px-8 py-4 text-lg w-full max-w-md mx-auto ${evaluating ? 'animate-pulse' : ''}`}
        >
          {evaluating ? (
            <>
              <Activity className="w-5 h-5 animate-spin" />
              Agents are analyzing...
            </>
          ) : (
            <>
              <Sparkles className="w-5 h-5" />
              Trigger Multi-Agent Evaluator
            </>
          )}
        </button>
      </div>
    );
  }

  // Formatting data for Radar chart
  const radarData = [
    { subject: 'Requirements', A: 8, fullMark: 10 },
    { subject: 'Architecture', A: 9, fullMark: 10 },
    { subject: 'Design', A: 7, fullMark: 10 },
    { subject: 'Testing', A: 4, fullMark: 10 },
    { subject: 'Prompt Eng.', A: 12, fullMark: 15 },
    { subject: 'RAG/Agents', A: 14, fullMark: 15 },
    { subject: 'Guardrails', A: 2, fullMark: 5 },
  ]; // Note: MVP mocks radar categories, but uses actual final scores below

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      <header className="flex items-start justify-between">
        <div>
          <h1 className="text-4xl font-bold mb-2">{submission.projectTitle}</h1>
          <p className="text-xl text-textMuted">Team: {submission.teamName}</p>
        </div>
        <div className="glass-card px-8 py-6 text-center shadow-primary/10 border-primary/30">
          <p className="text-sm text-textMuted uppercase font-bold tracking-wider mb-1">Final Score</p>
          <div className="text-5xl font-black text-transparent bg-clip-text bg-gradient-to-r from-primary to-accent">
            {result.overallScore}
            <span className="text-2xl text-textMuted">/100</span>
          </div>
        </div>
      </header>

      {/* Recommended Verdict */}
      <div className="glass-card p-8 border-l-4 border-l-primary bg-gradient-to-r from-primary/10 to-transparent">
        <h3 className="flex items-center gap-2 font-bold text-lg mb-4 text-primary">
          <Star className="w-5 h-5" /> Head Judge Final Verdict
        </h3>
        <p className="text-lg leading-relaxed text-textMain/90">
          {result.finalRecommendation || "No final verdict provided by the consensus agent."}
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Col: High level Scores */}
        <div className="lg:col-span-1 space-y-6">
          <div className="glass-card p-6">
             <h3 className="font-bold text-lg mb-6 text-textMuted">Score Breakdown</h3>
             <div className="space-y-6">
               <div>
                 <div className="flex justify-between mb-2">
                   <span className="font-medium text-blue-400">Software Engineering</span>
                   <span className="font-bold">{result.softwareEngineeringScore}/50</span>
                 </div>
                 <div className="h-3 bg-white/5 rounded-full overflow-hidden">
                   <div 
                    className="h-full bg-blue-500 rounded-full" 
                    style={{ width: `${(result.softwareEngineeringScore/50)*100}%` }}
                   />
                 </div>
               </div>
               
               <div>
                 <div className="flex justify-between mb-2">
                   <span className="font-medium text-purple-400">GenAI & Prompting</span>
                   <span className="font-bold">{result.genAiScore}/50</span>
                 </div>
                 <div className="h-3 bg-white/5 rounded-full overflow-hidden">
                   <div 
                    className="h-full bg-purple-500 rounded-full" 
                    style={{ width: `${(result.genAiScore/50)*100}%` }}
                   />
                 </div>
               </div>
             </div>
          </div>

          <div className="glass-card p-6 h-[300px] flex flex-col justify-center">
             <h3 className="font-bold text-lg mb-2 text-textMuted text-center">Score Profile</h3>
             <ResponsiveContainer width="100%" height="100%">
              <RadarChart cx="50%" cy="50%" outerRadius="70%" data={radarData}>
                <PolarGrid stroke="rgba(255,255,255,0.1)" />
                <PolarAngleAxis dataKey="subject" tick={{ fill: '#94a3b8', fontSize: 11 }} />
                <Radar name="Score" dataKey="A" stroke="#818cf8" fill="#6366f1" fillOpacity={0.4} />
              </RadarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Right Col: Detailed Agent Feedback */}
        <div className="lg:col-span-2 space-y-6">
          <h3 className="font-bold text-2xl mb-4 text-white">Agent Sub-Reports</h3>
          <div className="grid gap-6">
            {result.agentFeedbacks.map((feedback, idx) => {
              const bgClass = AGENT_COLORS[feedback.agentName] || "text-white bg-white/10";
              return (
                <motion.div 
                  key={idx}
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: idx * 0.1 }}
                  className="glass-card p-6 border-l-4 group"
                  style={{ borderLeftColor: bgClass.split('text-')[1]?.split(' ')[0] }}
                >
                  <div className="flex justify-between items-start mb-4">
                    <div className={`flex items-center gap-3 font-bold text-lg ${bgClass.split(' ')[0]}`}>
                      <div className={`p-2 rounded-lg ${bgClass}`}>
                         {AGENT_ICONS[feedback.agentName] || <Bot className="w-5 h-5"/>}
                      </div>
                      {feedback.agentName}
                    </div>
                    {feedback.scoreGiven > 0 && (
                      <div className="font-bold text-xl px-4 py-1.5 rounded-xl bg-white/5">
                        {feedback.scoreGiven} <span className="text-sm text-textMuted font-normal">pts</span>
                      </div>
                    )}
                  </div>
                  
                  <div className="prose prose-invert max-w-none">
                    <p className="text-textMain/80 leading-relaxed whitespace-pre-wrap text-sm">
                      {feedback.detailedFeedback}
                    </p>
                  </div>
                  
                  {feedback.rubricBreakdownJson && feedback.rubricBreakdownJson !== "null" && (
                    <div className="mt-4 pt-4 border-t border-white/5">
                      <p className="text-xs text-textMuted font-mono bg-black/30 p-3 rounded-lg overflow-x-auto">
                        {feedback.rubricBreakdownJson}
                      </p>
                    </div>
                  )}
                </motion.div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

export default EvaluationReport;
